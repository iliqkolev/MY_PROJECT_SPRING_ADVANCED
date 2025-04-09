package MyChillZone.web;

import MyChillZone.category.model.service.CategoryService;
import MyChillZone.movie.model.Genre;
import MyChillZone.movie.model.Movie;
import MyChillZone.security.AuthenticationMetadata;
import MyChillZone.user.model.User;
import MyChillZone.user.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    private final UserService userService;

    @Autowired
    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getCategoryPage(@AuthenticationPrincipal AuthenticationMetadata a){

        User user = userService.getById(a.getUserId());

        ModelAndView modelAndView = new ModelAndView("category");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/results")
    public ModelAndView findMoviesByCategory(@RequestParam("category") Genre category, @AuthenticationPrincipal AuthenticationMetadata a){

        User user = userService.getById(a.getUserId());

        List<Movie> searchResults = categoryService.findMoviesByCategory(Genre.valueOf(String.valueOf(category)));

        ModelAndView modelAndView = new ModelAndView("category");
        modelAndView.addObject("searchResults", searchResults);
        modelAndView.addObject("category", category);
        modelAndView.addObject("user", user);

        return modelAndView;
    }








}
