package MyChillZone.user;

import MyChillZone.exception.UsernameAlreadyExistException;
import MyChillZone.notification.client.service.NotificationService;
import MyChillZone.subscription.model.Subscription;
import MyChillZone.subscription.model.service.SubscriptionService;
import MyChillZone.user.model.Country;
import MyChillZone.user.model.User;
import MyChillZone.user.model.repository.UserRepository;
import MyChillZone.user.model.service.UserService;
import MyChillZone.wallet.model.Wallet;
import MyChillZone.wallet.model.service.WalletService;
import MyChillZone.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private WalletService walletService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserService userService;


    //Test 1: When user exist with this username -> exception
    @Test
    void givenExistingUsername_whenRegister_thenExceptionIsThrown(){
        //Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .password("123123")
                .email("vik@abv.bg")
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        //When
        assertThrows(UsernameAlreadyExistException.class, () -> userService.register(registerRequest));
        verify(userRepository,never()).save(any());
        verify(subscriptionService, never()).createDefaultSubscription(any());
        verify(walletService, never()).createDefaultWallet(any());
        verify(notificationService, never()).saveNotificationPreference(any(UUID.class), anyBoolean(), anyString());
    }

    //Test 2: Happy path Registration
    @Test
    void givenHappyPath_whenRegister(){
        //Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .password("123123")
                .email("vik@abv.bg")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(user);
        when(subscriptionService.createDefaultSubscription(user)).thenReturn(new Subscription());
        when(walletService.createDefaultWallet(user)).thenReturn(new Wallet());

        //When
        User registeredUser = userService.register(registerRequest);

        //Then
        assertThat(registeredUser.getSubscriptions()).hasSize(1);
        assertThat(registeredUser.getWallets()).hasSize(1);
        verify(notificationService, times(1)).saveNotificationPreference(user.getId(), true, user.getEmail());
    }

}
