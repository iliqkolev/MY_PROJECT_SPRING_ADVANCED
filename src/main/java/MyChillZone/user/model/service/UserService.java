package MyChillZone.user.model.service;

import MyChillZone.exception.DomainException;
import MyChillZone.exception.UsernameAlreadyExistException;
import MyChillZone.notification.client.service.NotificationService;
import MyChillZone.security.AuthenticationMetadata;
import MyChillZone.subscription.model.Subscription;
import MyChillZone.subscription.model.service.SubscriptionService;
import MyChillZone.user.model.Country;
import MyChillZone.user.model.User;
import MyChillZone.user.model.UserRole;
import MyChillZone.user.model.repository.UserRepository;
import MyChillZone.wallet.model.Wallet;
import MyChillZone.wallet.model.service.WalletService;
import MyChillZone.web.dto.RegisterRequest;
import MyChillZone.web.dto.UserEditRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionService subscriptionService;
    private final WalletService walletService;
    private final NotificationService notificationService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SubscriptionService subscriptionService, WalletService walletService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionService = subscriptionService;
        this.walletService = walletService;
        this.notificationService = notificationService;
    }

    @Transactional
    public User register(RegisterRequest registerRequest) {

        Optional<User> userOptional = userRepository.findByUsername(registerRequest.getUsername());
        if (userOptional.isPresent()){
            throw new UsernameAlreadyExistException("Username [%s] already exist.".formatted(registerRequest.getUsername()));
        }

        User user =userRepository.save(initializeUser(registerRequest));

        Subscription defaultSubscription = subscriptionService.createDefaultSubscription(user);
        user.setSubscriptions(List.of(defaultSubscription));

        Wallet defaultWallet = walletService.createDefaultWallet(user);
        user.setWallets(List.of(defaultWallet));


        notificationService.saveNotificationPreference(user.getId(), true, user.getEmail());

        String emailBody = "Hey movie lover! \uD83C\uDFA5✨ myChillZone = ANY film you crave! Action\uD83D\uDCA5, Romance\uD83D\uDC96, Comedy\uD83D\uDE02, Horror\uD83D\uDC7B, Sci-Fi\uD83D\uDE80, Drama\uD83C\uDFAD. Popcorn ready? PLAY!";
        notificationService.sendNotification(user.getId(), "Welcome to MyChillZone", emailBody);


        log.info("Successfully created new user account for username [%s] and id [%s]".formatted(user.getUsername(), user.getId()));

        return user;
    }

    private User initializeUser(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .userRole(UserRole.USER)
                .isActive(true)
                .email(registerRequest.getEmail())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }
    public User getById(UUID userId) {

        Optional<User> user = userRepository.findById(userId);
        user.orElseThrow(()-> new DomainException("User with id [%s] does not exist".formatted(userId)));

        return user.get();

    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getByUsername(username);
        return new AuthenticationMetadata(user.getId(), username, user.getPassword(), user.getUserRole(), user.isActive());

    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new DomainException("User with this username does not exist"));
    }


    public void editUserProfile(UUID userId, UserEditRequest userEditRequest) {

        User user = getById(userId);

        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setEmail(userEditRequest.getEmail());
        user.setCountry(Country.valueOf(userEditRequest.getCountry()));
        user.setProfilePicture(userEditRequest.getProfilePicture());
        user.setUpdatedOn(LocalDateTime.now());


        if (userEditRequest.getEmail().isBlank()){
            notificationService.saveNotificationPreference(userId, false, "Edit an email if you want go receive");
        }else {
            notificationService.saveNotificationPreference(userId, true, userEditRequest.getEmail());
        }

        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void switchStatus(UUID userId) {
        User user = getById(userId);

       user.setActive(!user.isActive());
       userRepository.save(user);
    }

    public void switchRole(UUID userId) {

        User user = getById(userId);

        if (user.getUserRole() == UserRole.USER){
            user.setUserRole(UserRole.ADMIN);
        }else{
            user.setUserRole(UserRole.USER);
        }

        userRepository.save(user);
    }
}
