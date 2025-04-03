package MyChillZone.web;

import MyChillZone.exception.UsernameAlreadyExistException;
import MyChillZone.security.AuthenticationMetadata;
import MyChillZone.user.model.UserRole;
import MyChillZone.user.model.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


import java.util.UUID;

import static MyChillZone.TestBuilder.aRandomUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(IndexController.class )
public class IndexControllerApiTest {

    // ВАЖНО: Когато тествам контролери трябва да МОКНА всички депендънсита на този контролер с анотация @MockitoBean!!!
    @MockitoBean
    private UserService userService;

    // Използвам MockMvc за да изпращам заявки
    @Autowired
    private MockMvc mockMvc;

    //INDEX
    @Test
    void getRequestToIndexEndpoint_shouldReturnIndexView() throws Exception {

        //1. Build request
        MockHttpServletRequestBuilder request = get("/");

        //2. Send request
        //.andExpect(status().isOk()); -> проверявам резултата
        //MockMvcResultMatchers.status -> проверка на статуса
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
    //REGISTER
    @Test
    void getRequestToRegisterEndpoint_shouldReturnRegisterView() throws Exception {

        MockHttpServletRequestBuilder request = get("/register");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    void postRequestToRegisterEndpoint_happyPath() throws Exception {

        MockHttpServletRequestBuilder request = post("/register")
                .formField("username", "iliqkolev")
                .formField("password","123123")
                .formField("email", "ichko.3@abv.bg")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).register(any());
    }
    @Test
    void postRequestToRegisterEndpoint_whenUsernameAlreadyExist_thenRedirectToRegisterWithFlashParameter() throws Exception {

        when(userService.register(any())).thenThrow(new UsernameAlreadyExistException("Username already exist"));

        MockHttpServletRequestBuilder request = post("/register")
                .formField("username", "iliqkolev")
                .formField("password","123123")
                .formField("email", "ichko.3@abv.bg")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attributeExists("usernameAlreadyExistMessage"));

        verify(userService, times(1)).register(any());
    }
    @Test
    void postRequestToRegisterEndpoint_withInvalidData_returnRegisterView() throws Exception {

        MockHttpServletRequestBuilder request = post("/register")
                .formField("username", "")
                .formField("password","123123")
                .formField("email", "ichko.3@abv.bg")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).register(any());
    }
    //LOGIN
    @Test
    void getRequestToLoginEndpoint_shouldReturnLoginView() throws Exception {

        MockHttpServletRequestBuilder request = get("/login");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest"));
    }

    @Test
    void getRequestToLoginEndpointWithErrorParameter_shouldReturnLoginViewAndErrorMessageAttribute() throws Exception {

        MockHttpServletRequestBuilder request = get("/login").param("error", "");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    //HOME
    @Test
    void getAuthenticatedRequestToHome_returnHomeView() throws Exception {

        when(userService.getById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "Iliq", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder request = get("/home")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user"));

        verify(userService, times(1)).getById(any());
    }

    @Test
    void getUnauthenticatedRequestToHome_redirectToLogin() throws Exception {

        MockHttpServletRequestBuilder request = get("/home");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());

        verify(userService, never()).getById(any());
    }

    //CONTACTS
    @Test
    void getRequestToContactEndpoint_shouldReturnContactView() throws Exception {

        when(userService.getById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "Iliq", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder request = get("/contacts")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("contact-us"))
                .andExpect(model().attributeExists("user"));
    }





}
