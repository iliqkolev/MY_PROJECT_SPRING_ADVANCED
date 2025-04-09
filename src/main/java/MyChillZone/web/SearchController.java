package MyChillZone.web;

import MyChillZone.movie.model.Movie;
import MyChillZone.search.model.service.SearchService;
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
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    private final UserService userService;

    @Autowired
    public SearchController(SearchService searchService, UserService userService) {
        this.searchService = searchService;
        this.userService = userService;
    }


    @GetMapping
    public ModelAndView getSearchPage(@AuthenticationPrincipal AuthenticationMetadata a){

        User user = userService.getById(a.getUserId());

        ModelAndView modelAndView = new ModelAndView("search");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/results")
    public  ModelAndView searchMovie(@RequestParam("movie") String movie, @AuthenticationPrincipal AuthenticationMetadata a) {
        User user = userService.getById(a.getUserId());

        List<Movie> searchResults = searchService.searchMovie(movie);

        ModelAndView modelAndView = new ModelAndView("search");
        if (searchResults.isEmpty()) {
            modelAndView.addObject("message", "No movies found for your search.");
        } else {
            modelAndView.addObject("searchResults", searchResults);
        }
        modelAndView.addObject("movie", movie);
        modelAndView.addObject("user", user);

        return modelAndView;
    }








}
