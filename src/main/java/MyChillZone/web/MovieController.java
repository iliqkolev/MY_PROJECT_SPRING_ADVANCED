package MyChillZone.web;

import MyChillZone.movie.model.Genre;
import MyChillZone.movie.model.Movie;
import MyChillZone.movie.model.repository.MovieRepository;
import MyChillZone.movie.model.service.MovieService;
import MyChillZone.security.AuthenticationMetadata;
import MyChillZone.user.model.User;
import MyChillZone.user.model.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/movies")
public class MovieController {
    private final UserService userService;
    private final MovieRepository movieRepository;
    private final MovieService movieService;

    public static final List<Genre> DEFAULT_GENRE_ORDER = List.of(Genre.ACTION, Genre.COMEDY, Genre.HORROR, Genre.FANTASY);


    @Autowired
    public MovieController(UserService userService, MovieRepository movieRepository, MovieService movieService) {
        this.userService = userService;
        this.movieRepository = movieRepository;
        this.movieService = movieService;
    }

    @GetMapping
    public ModelAndView getMoviePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){

        UUID userId = authenticationMetadata.getUserId();
        User user = userService.getById(userId);

        Map<Genre, List<Movie>> moviesByGenre = movieService.getMoviesGroupedByGenre(DEFAULT_GENRE_ORDER);

        ModelAndView modelAndView = new ModelAndView("movies");
        modelAndView.addObject("user", user);
        modelAndView.addObject("moviesByGenre", moviesByGenre);

        return modelAndView;
    }

    @GetMapping("/favourites")
    public ModelAndView getFavouritePage(@AuthenticationPrincipal AuthenticationMetadata auth){

        UUID userId = auth.getUserId();
        User user = userService.getById(userId);

        List<Movie> favMovies = user.getFavouriteMovies();
        Map<Genre, List<Movie>> sortedFavByGenre = movieService.groupMoviesByFavourite(favMovies, DEFAULT_GENRE_ORDER);

        ModelAndView modelAndView = new ModelAndView("favourite-movies");
        modelAndView.addObject("user", user);
        modelAndView.addObject("favByGenre", sortedFavByGenre);

        return modelAndView;
    }


    @GetMapping("/liked")
    public ModelAndView getMostLikedMoviesPage(@AuthenticationPrincipal AuthenticationMetadata auth){

        UUID userId = auth.getUserId();
        User user = userService.getById(userId);

        Map<Genre, List<Movie>> sortedLikedByGenre = movieService.getTop3LikedMoviesByGenre();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("liked-movies");
        modelAndView.addObject("likedByGenre", sortedLikedByGenre);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/{id}/favourites")
    public String addToFavouritesMovie(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata a){

        movieService.addMovieToFavourites(id, a.getUserId());

        return "redirect:/movies";
    }

    @PostMapping("/{id}/like")
    public String addToLikes(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata a){

        movieService.addMovieToLikes(id, a.getUserId());

        return "redirect:/movies";
    }

    @DeleteMapping("/{id}/favourites/remove")
    public String removeFavouriteMovie(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata a){

        movieService.removeFavouriteMovie(id, a.getUserId());

        return "redirect:/movies/favourites";
    }



}
