package MyChillZone.web;

import MyChillZone.exception.DomainException;
import MyChillZone.security.AuthenticationMetadata;
import MyChillZone.user.model.User;
import MyChillZone.user.model.UserRole;
import MyChillZone.user.model.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    //SWITCH_ROLE
    @Test
    void putUnauthorizedRequestToSwitchRole_shouldReturn404AndNotFoundView() throws Exception {

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "Iliq", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder request = put("/users/{id}/role", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    void putAuthorizedRequestToSwitchRole_shouldRedirectToUsers() throws Exception {

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "Iliq", "123123", UserRole.ADMIN, true);

        MockHttpServletRequestBuilder request = put("/users/{id}/role", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService, times(1)).switchRole(any());
    }

    //SWITCH_STATUS
    @Test
    void testSwitchUserStatus_AsAdmin_ShouldRedirect() throws Exception {

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "Iliq", "123123", UserRole.ADMIN, true);

        MockHttpServletRequestBuilder request = put("/users/{id}/status", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService, times(1)).switchStatus(any());
    }

    @Test
    void testSwitchUserStatus_AsUser_ShouldBeReturn403() throws Exception {

        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "Iliq", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder request = put("/users/{id}/status", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());

        verify(userService, times(0)).switchStatus(any());
    }

    //PROFILE MENU
//    @Test
//    void testGetProfileMenu() throws Exception {
//        // Потребителски ID за теста
//        UUID userId = UUID.randomUUID();
//
//        // Мокваме потребителските данни
//        User mockUser = new User();
//        mockUser.setId(userId);
//        mockUser.setUsername("testUser");
//        mockUser.setEmail("test@example.com");
//
//        // Мокваме UserService да върне същия потребител
//        when(userService.getById(userId)).thenReturn(mockUser);
//
//        // Създаваме GET заявка за профила на потребителя
//        MockHttpServletRequestBuilder request = get("/users/{id}/profile", userId);
//
//        // Изпълняваме заявката и проверяваме резултатите
//        mockMvc.perform(request)
//                .andExpect(status().isOk())  // Статус код 200 OK
//                .andExpect(view().name("profile-menu"))  // Проверяваме името на изгледа
//                .andExpect(model().attributeExists("user"))  // Проверяваме дали потребителят е добавен в модела
//                .andExpect(model().attributeExists("userEditRequest"));  // Проверяваме дали обектът за редактиране на потребител е добавен в модела
//    }
//
//    @Test
//    void testGetProfileMenu_UserNotFound() throws Exception {
//        // Потребителски ID за теста
//        UUID userId = UUID.randomUUID();
//
//        // Мокваме UserService да хвърли DomainException, ако потребителят не е намерен
//        when(userService.getById(userId)).thenThrow(new DomainException("User with id [%s] does not exist".formatted(userId)));
//
//        // Създаваме GET заявка за профила на потребителя
//        MockHttpServletRequestBuilder request = get("/users/{id}/profile", userId);
//
//        // Изпълняваме заявката и проверяваме резултатите
//        mockMvc.perform(request)
//                .andExpect(status().isNotFound())  // Статус код 404 Not Found
//                .andExpect(view().name("not-found"));  // Очакваме изглед "not-found"
//    }







}
