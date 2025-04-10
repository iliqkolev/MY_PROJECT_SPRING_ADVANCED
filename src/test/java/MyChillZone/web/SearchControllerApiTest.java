package MyChillZone.web;

import MyChillZone.movie.model.Genre;
import MyChillZone.movie.model.Movie;
import MyChillZone.search.model.service.SearchService;
import MyChillZone.security.AuthenticationMetadata;
import MyChillZone.user.model.User;
import MyChillZone.user.model.UserRole;
import MyChillZone.user.model.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static MyChillZone.TestBuilder.aRandomUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
public class SearchControllerApiTest {
    @MockitoBean
    private  SearchService searchService;
    @MockitoBean
    private  UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToSearchEndpoint_shouldReturnSearchView() throws Exception{

        when(userService.getById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal =  new AuthenticationMetadata(userId, "Iliq", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder request = get("/search")
                .with(user(principal));


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("user"));

        verify(userService, times(1)).getById(any());
    }

//    @Test
//    void getRequestToSearchMovie_shouldReturnResultView()throws Exception{
//
//
//        when(userService.getById(any())).thenReturn(aRandomUser());
//
//        Movie movie1 = new Movie();
//        movie1.setTitle("Movie 1");
//        movie1.setGenre(Genre.ACTION);
//
//        List<Movie> searchResult = List.of(movie1);
//
//        when(searchService.searchMovie(any())).thenReturn(searchResult);
//
//        UUID userId = UUID.randomUUID();
//        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "Iliq", "123123", UserRole.USER, true);
//
//        MockHttpServletRequestBuilder request = get("/category/results?movie=someMovieName")
//                .with(user(principal));
//
//        mockMvc.perform(request)
//                .andExpect(status().isOk())
//                .andExpect(view().name("search"))
//                .andExpect(model().attributeExists("user"))
//                .andExpect(model().attributeExists("movie"));
//
//        verify(userService, times(1)).getById(any());
//    }




}
