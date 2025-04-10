package MyChillZone.web;

import MyChillZone.category.model.service.CategoryService;
import MyChillZone.movie.model.Genre;
import MyChillZone.movie.model.Movie;
import MyChillZone.security.AuthenticationMetadata;
import MyChillZone.user.model.UserRole;
import MyChillZone.user.model.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static MyChillZone.TestBuilder.aRandomUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.List;
import java.util.UUID;



@WebMvcTest(CategoryController.class)
public class CategoryControllerApiTest {
    @MockitoBean
    private  CategoryService categoryService;
    @MockitoBean
    private  UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToCategoryEndpoint_shouldReturnIndexView() throws Exception {

        when(userService.getById(any())).thenReturn(aRandomUser());

        UUID userId=UUID.randomUUID();
        AuthenticationMetadata principal =  new AuthenticationMetadata(userId, "Iliq", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder request = get("/category")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("category"))
                .andExpect(model().attributeExists("user"));
        verify(userService, times(1)).getById(any());
    }

    @Test
    void getRequestToFindMovieByCategoryEndpoint_shouldReturnResultView() throws Exception {

        when(userService.getById(any())).thenReturn(aRandomUser());

        Movie movie1 = new Movie();
        movie1.setTitle("Movie 1");
        movie1.setGenre(Genre.ACTION);

        Movie movie2 = new Movie();
        movie2.setTitle("Movie 2");
        movie2.setGenre(Genre.FANTASY);

        List<Movie> searchResult = List.of(movie1, movie2);

        when(categoryService.findMoviesByCategory(any())).thenReturn(searchResult);

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "Iliq", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder request = get("/category/results?category=ACTION")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("category"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("searchResults"))
                .andExpect(model().attribute("searchResults", searchResult))
                .andExpect(model().attribute("category", Genre.ACTION));

        verify(userService, times(1)).getById(any());
    }

}
