package MyChillZone.web;

import MyChillZone.user.model.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest(IndexController.class )
public class IndexControllerApiTest {

    // ВАЖНО: Когато тествам контролери трябва да МОКНА всички депендънсита на този контролер с анотация @MockitoBean!!!
    @MockitoBean
    private UserService userService;

    // Използвам MockMvc за да изпращам заявки
    @Autowired
    private MockMvc mockMvc;

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




}
