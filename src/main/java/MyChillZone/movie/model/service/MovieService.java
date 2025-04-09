package MyChillZone.movie.model.service;

import MyChillZone.movie.model.Genre;
import MyChillZone.movie.model.Movie;
import MyChillZone.movie.model.repository.MovieRepository;
import MyChillZone.user.model.User;
import MyChillZone.user.model.repository.UserRepository;
import MyChillZone.user.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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



    public Map<Genre, List<Movie>> getMoviesGroupedByGenre(List<Genre> genreOrder) {
        List<Movie> movies = getAllMovies();

        Map<Genre, List<Movie>> grouped = movies.stream()
                .collect(Collectors.groupingBy(Movie::getGenre));

        Map<Genre, List<Movie>> ordered = new LinkedHashMap<>();
        for (Genre genre : genreOrder) {
            if (grouped.containsKey(genre)) {
                ordered.put(genre, grouped.get(genre));
            }
        }

        return ordered;
    }

    public Map<Genre, List<Movie>> groupMoviesByFavourite(List<Movie> favMovies, List<Genre> genreOrder) {
        Map<Genre, List<Movie>> grouped = favMovies.stream()
                .collect(Collectors.groupingBy(Movie::getGenre));

        Map<Genre, List<Movie>> ordered = new LinkedHashMap<>();
        for (Genre genre : genreOrder) {
            if (grouped.containsKey(genre)) {
                ordered.put(genre, grouped.get(genre));
            }
        }

        return ordered;
    }


    public Map<Genre, List<Movie>> getTop3LikedMoviesByGenre() {
        // Вземаме всички филми с поне 1 лайк
        List<Movie> allMovies = movieRepository.findAll().stream()
                .filter(movie -> movie.getLikes() > 0)
                .collect(Collectors.toList());

        // Групираме ги по жанр
        Map<Genre, List<Movie>> groupedByGenre = allMovies.stream()
                .collect(Collectors.groupingBy(Movie::getGenre));

        // Сортираме филмите във всяка група по лайкове и вземаме топ 3
        Map<Genre, List<Movie>> top3ByGenre = new LinkedHashMap<>();
        for (Map.Entry<Genre, List<Movie>> entry : groupedByGenre.entrySet()) {
            List<Movie> sortedMovies = entry.getValue().stream()
                    .sorted((m1, m2) -> Integer.compare(m2.getLikes(), m1.getLikes())) // сортиране по лайкове (от най-голям към най-малък)
                    .limit(3) // вземаме само първите 3
                    .collect(Collectors.toList());

            top3ByGenre.put(entry.getKey(), sortedMovies);
        }

        return top3ByGenre;
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


    public void removeFavouriteMovie(UUID favouriteMovieId, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Movie movie = movieRepository.findById(favouriteMovieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        user.getFavouriteMovies().remove(movie);

        userRepository.save(user);
    }
}
