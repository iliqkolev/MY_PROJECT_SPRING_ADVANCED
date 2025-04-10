package MyChillZone.user;

import MyChillZone.exception.DomainException;
import MyChillZone.exception.UsernameAlreadyExistException;
import MyChillZone.notification.client.service.NotificationService;
import MyChillZone.security.AuthenticationMetadata;


import MyChillZone.user.model.Country;
import MyChillZone.user.model.User;
import MyChillZone.user.model.UserRole;
import MyChillZone.user.model.repository.UserRepository;
import MyChillZone.user.model.service.UserService;


import MyChillZone.web.dto.RegisterRequest;
import MyChillZone.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private NotificationService notificationService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    private static Stream<Arguments> userRolesArguments(){
        return Stream.of(
                Arguments.of(UserRole.USER, UserRole.ADMIN),
                Arguments.of(UserRole.ADMIN, UserRole.USER)
        );
    }
    @ParameterizedTest
    @MethodSource("userRolesArguments")
    void whenChangeRoleUserRole_theCorrectRoleIsAssigned(UserRole currentUserRole, UserRole expectedUserRole){
        //Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .userRole(currentUserRole)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //When
        userService.switchRole(userId);

        //Then
        assertEquals(expectedUserRole, user.getUserRole());
    }


    @Test
    void givenExistingUsersInDatabase_whenGetAllUsers_thenReturnThemAll(){

        //Given
        List<User> userList = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(userList);

        //When
        List<User> users = userService.getAllUsers();
        //Then
        assertThat(userList).hasSize(2);
    }

    //Test 1: When user exist with this username -> exception
    @Test
    void givenExistingUsername_whenRegister_thenExceptionIsThrown(){
        //Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Ichkata")
                .password("123123")
                .email("ichko.3@abv.bg")
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        //When
        assertThrows(UsernameAlreadyExistException.class, () -> userService.register(registerRequest));
        verify(userRepository,never()).save(any());
        verify(notificationService, never()).saveNotificationPreference(any(UUID.class), anyBoolean(), anyString());
        verify(notificationService, never()).sendNotification(any(UUID.class), anyString(), anyString());
    }

    //Test 2: Happy path Registration
    @Test
    void givenHappyPath_whenRegister(){
        //Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Ichkata")
                .password("123123")
                .email("ichko.3@abv.bg")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("Ichkata")
                .password("123123")
                .email("ichko.3@abv.bg")
                .build();

        String emailBody = "Hey movie lover! \uD83C\uDFA5✨ myChillZone = ANY film you crave! Action\uD83D\uDCA5, Romance\uD83D\uDC96, Comedy\uD83D\uDE02, Horror\uD83D\uDC7B, Sci-Fi\uD83D\uDE80, Drama\uD83C\uDFAD. Popcorn ready? PLAY!";

        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(user);


        //When
        User registeredUser = userService.register(registerRequest);

        //Then
        verify(notificationService, times(1)).saveNotificationPreference(user.getId(), true, user.getEmail());
        verify(notificationService,times(1)).sendNotification(user.getId(), "Welcome to MyChillZone", emailBody);
    }


    // Test 2: When user doesn't exist throw exception
    @Test
    void givenMissingUserFromDatabase_whenLoadUserByUsername_thenThrowException(){

        //Given
        String username = "Ichkata";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(DomainException.class, () -> userService.loadUserByUsername(username));
    }

    // Test 1: When user exist - then return new AuthenticationMetadata
    @Test
    void givenExistingUser_whenLoadUserByUsername_thenReturnCorrectAuthenticationMetadata(){

        //Given
        String username = "Ichkata";
        User user= User.builder()
                .id(UUID.randomUUID())
                .isActive(true)
                .password("123123")
                .userRole(UserRole.ADMIN)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        //When
        UserDetails userDetails = userService.loadUserByUsername(username);

        //Then
        assertInstanceOf(AuthenticationMetadata.class, userDetails);
        AuthenticationMetadata result = (AuthenticationMetadata) userDetails;
        assertEquals(user.getId(), result.getUserId());
        assertEquals(username, result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.isActive(), result.isActive());
        assertEquals(user.getUserRole(), result.getRole());
        assertThat(result.getAuthorities()).hasSize(1);
        assertEquals("ROLE_ADMIN", result.getAuthorities().iterator().next().getAuthority());
    }


    // Test Case: When there is no user in the database(repository returns Optional.empty()) - then expect an exception of type DomainException is thrown
    @Test
    void givenMissingUserFromDatabase_whenEditUserProfile_thenExceptionIsThrown(){

        UUID  userId = UUID.randomUUID();
        UserEditRequest userEditRequest = UserEditRequest.builder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> userService.editUserProfile(userId, userEditRequest));
        verify(notificationService, never()).saveNotificationPreference(any(UUID.class), anyBoolean(), anyString());
        verify(userRepository, never()).save(any());
    }

    //Test Case: When database returns user object -> then change their details from the dto with email address
    //and save notification preference and save the user to database
    @Test
    void givenExistingUser_whenEditTheirProfileWithActualEmail_thenChangeTheirDetailsSaveNotificationPreferenceAndSaveToDatabase(){

        //Given
        UUID  userId = UUID.randomUUID();
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .firstName("Ichkata")
                .lastName("Kolev")
                .email("ichko.3@abv.bg")
                .country("BULGARIA")
                .profilePicture("www.image.com")
                .build();

        User user= User.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //When
        userService.editUserProfile(userId,userEditRequest);

        //Then
        assertEquals("Ichkata", user.getFirstName());
        assertEquals("Kolev", user.getLastName());
        assertEquals("ichko.3@abv.bg", user.getEmail());
        assertEquals(Country.BULGARIA, user.getCountry());
        assertEquals("www.image.com", user.getProfilePicture());

        verify(notificationService, times(1)).saveNotificationPreference(userId, true, userEditRequest.getEmail());
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void givenExistingUser_whenEditTheirProfileWithEmptyEmail_thenChangeTheirDetailsSaveNotificationPreferenceAndSaveToDatabase(){

        //Given
        UUID  userId = UUID.randomUUID();
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .firstName("Ichkata")
                .lastName("Kolev")
                .email("")
                .country("BULGARIA")
                .profilePicture("www.image.com")
                .build();

        User user= User.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //When
        userService.editUserProfile(userId,userEditRequest);

        //Then
        assertEquals("Ichkata", user.getFirstName());
        assertEquals("Kolev", user.getLastName());
        assertEquals("", user.getEmail());
        assertEquals(Country.BULGARIA, user.getCountry());
        assertEquals("www.image.com", user.getProfilePicture());

        verify(notificationService, times(1)).saveNotificationPreference(userId, false, "Edit an email if you want go receive");
        verify(userRepository, times(1)).save(user);
    }

    //Switch ROLE
    @Test
    void givenUserWithRoleAdmin_whenSwitchRole_thenUserReceivesUserRole() {

        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .userRole(UserRole.ADMIN)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.switchRole(userId);


        // Then
        assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void givenUserWithRoleUser_whenSwitchRole_thenUserReceivesAdminRole() {

        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .userRole(UserRole.USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.switchRole(userId);

        // Then
        assertThat(user.getUserRole()).isEqualTo(UserRole.ADMIN);
    }


    //Switch Status
    @Test
    void givenUserWithStatusActive_whenSwitchStatus_thenUserStatusBecomeInactive(){
        User user = User.builder()
                .id(UUID.randomUUID())
                .isActive(true)
                .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //When
        userService.switchStatus(user.getId());

        //Then
        assertFalse(user.isActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenUserWithStatusInactive_whenSwitchStatus_thenUserStatusBecomeActive(){
        User user = User.builder()
                .id(UUID.randomUUID())
                .isActive(false)
                .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //When
        userService.switchStatus(user.getId());

        //Then
        assertTrue(user.isActive());
        verify(userRepository, times(1)).save(user);
    }





}
