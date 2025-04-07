package MyChillZone.movie.model.service;

import MyChillZone.movie.model.Movie;
import MyChillZone.movie.model.repository.MovieRepository;
import MyChillZone.user.model.User;
import MyChillZone.user.model.repository.UserRepository;
import MyChillZone.user.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public MovieService(MovieRepository movieRepository, UserRepository userRepository, UserService userService) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getById(UUID id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
    }

    public void addMovieToFavourites(UUID movieId, UUID userId) {

        User user = userService.getById(userId);
        Movie movie = getById(movieId);

        if (!user.getFavouriteMovies().contains(movie)){
            user.getFavouriteMovies().add(movie);
            userRepository.save(user);
        }
    }

    public void addMovieToLikes(UUID movieId, UUID userId) {
        User user = userService.getById(userId);
        Movie movie = getById(movieId);

        if (!user.getLikedMovies().contains(movie)){
            user.getLikedMovies().add(movie);
            userRepository.save(user);
        }

        movie.setLikes(movie.getLikes() + 1);
        movieRepository.save(movie);
    }

    public int getLikesCount(UUID movieId){
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found"));
        return movie.getLikes();
    }
}
