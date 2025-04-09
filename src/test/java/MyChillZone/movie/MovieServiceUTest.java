package MyChillZone.movie;

import MyChillZone.movie.model.Genre;
import MyChillZone.movie.model.Movie;
import MyChillZone.movie.model.repository.MovieRepository;
import MyChillZone.movie.model.service.MovieService;
import MyChillZone.user.model.User;
import MyChillZone.user.model.repository.UserRepository;
import MyChillZone.user.model.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceUTest {
    @Mock
    private  MovieRepository movieRepository;
    @Mock
    private  UserRepository userRepository;
    @Mock
    private  UserService userService;

    @InjectMocks
    private MovieService movieService;

    @Test
    void testAddMovieToFavourites() {

        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setFavouriteMovies(new ArrayList<>());

        Movie movie = new Movie();
        movie.setId(movieId);

        when(userService.getById(userId)).thenReturn(user);
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        movieService.addMovieToFavourites(movieId, userId);

        assertTrue(user.getFavouriteMovies().contains(movie));
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void testAddMovieToLikes() {
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setLikedMovies(new ArrayList<>());

        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setLikes(10);

        when(userService.getById(userId)).thenReturn(user);
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        movieService.addMovieToLikes(movieId, userId);

        assertTrue(user.getLikedMovies().contains(movie));
        assertEquals(11, movie.getLikes());
        verify(userRepository, times(1)).save(user);
        verify(movieRepository, times(1)).save(movie);
    }


    @Test
    void testRemoveFavouriteMovie() {
        UUID favouriteMovieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        List<Movie> favouriteMovies = new ArrayList<>();
        Movie movie = new Movie();
        movie.setId(favouriteMovieId);
        favouriteMovies.add(movie);

        user.setFavouriteMovies(favouriteMovies);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(movieRepository.findById(favouriteMovieId)).thenReturn(Optional.of(movie));

        movieService.removeFavouriteMovie(favouriteMovieId, userId);

        assertFalse(user.getFavouriteMovies().contains(movie));

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRemoveFavouriteMovie_UserNotFound() {

        UUID favouriteMovieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {movieService.removeFavouriteMovie(favouriteMovieId, userId);}, "User not found");
    }

    @Test
    void testRemoveFavouriteMovie_MovieNotFound() {
        UUID favouriteMovieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        List<Movie> favouriteMovies = new ArrayList<>();
        Movie movie = new Movie();
        movie.setId(favouriteMovieId);
        favouriteMovies.add(movie);

        user.setFavouriteMovies(favouriteMovies);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(movieRepository.findById(favouriteMovieId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            movieService.removeFavouriteMovie(favouriteMovieId, userId);
        }, "Movie not found");
    }

    @Test
    void testGetAllMovies() {

        Movie movie1 = new Movie();
        movie1.setId(UUID.randomUUID());
        movie1.setTitle("Movie 1");

        Movie movie2 = new Movie();
        movie2.setId(UUID.randomUUID());
        movie2.setTitle("Movie 2");

        List<Movie> movies = Arrays.asList(movie1, movie2);


        when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = movieService.getAllMovies();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(movie1));
        assertTrue(result.contains(movie2));

        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void testGetMoviesGroupedByGenre() {

        Movie movie1 = new Movie();
        movie1.setId(UUID.randomUUID());
        movie1.setTitle("Movie 1");
        movie1.setGenre(Genre.ACTION);

        Movie movie2 = new Movie();
        movie2.setId(UUID.randomUUID());
        movie2.setTitle("Movie 2");
        movie2.setGenre(Genre.COMEDY);

        Movie movie3 = new Movie();
        movie3.setId(UUID.randomUUID());
        movie3.setTitle("Movie 3");
        movie3.setGenre(Genre.ACTION);

        List<Movie> movies = Arrays.asList(movie1, movie2, movie3);

        List<Genre> genreOrder = Arrays.asList(Genre.ACTION, Genre.COMEDY);

        when(movieRepository.findAll()).thenReturn(movies);

        Map<Genre, List<Movie>> result = movieService.getMoviesGroupedByGenre(genreOrder);


        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(Genre.ACTION));
        assertTrue(result.containsKey(Genre.COMEDY));

        assertEquals(2, result.get(Genre.ACTION).size());
        assertEquals(1, result.get(Genre.COMEDY).size());

        List<Genre> genresInResultOrder = new ArrayList<>(result.keySet());
        assertEquals(genreOrder, genresInResultOrder);

        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void testGroupMoviesByFavourite() {

        Movie movie1 = new Movie();
        movie1.setId(UUID.randomUUID());
        movie1.setTitle("Movie 1");
        movie1.setGenre(Genre.ACTION);

        Movie movie2 = new Movie();
        movie2.setId(UUID.randomUUID());
        movie2.setTitle("Movie 2");
        movie2.setGenre(Genre.COMEDY);

        Movie movie3 = new Movie();
        movie3.setId(UUID.randomUUID());
        movie3.setTitle("Movie 3");
        movie3.setGenre(Genre.ACTION);

        List<Movie> favMovies = Arrays.asList(movie1, movie2, movie3);

        List<Genre> genreOrder = Arrays.asList(Genre.ACTION, Genre.COMEDY);

        Map<Genre, List<Movie>> result = movieService.groupMoviesByFavourite(favMovies, genreOrder);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(Genre.ACTION));
        assertTrue(result.containsKey(Genre.COMEDY));


        assertEquals(2, result.get(Genre.ACTION).size());
        assertEquals(1, result.get(Genre.COMEDY).size());

        List<Genre> genresInResultOrder = new ArrayList<>(result.keySet());
        assertEquals(genreOrder, genresInResultOrder);
    }

    @Test
    void testGetTop3LikedMoviesByGenre() {

        Movie movie1 = new Movie();
        movie1.setId(UUID.randomUUID());
        movie1.setTitle("Action Movie 1");
        movie1.setGenre(Genre.ACTION);
        movie1.setLikes(5);

        Movie movie2 = new Movie();
        movie2.setId(UUID.randomUUID());
        movie2.setTitle("Action Movie 2");
        movie2.setGenre(Genre.ACTION);
        movie2.setLikes(10);

        Movie movie3 = new Movie();
        movie3.setId(UUID.randomUUID());
        movie3.setTitle("Action Movie 3");
        movie3.setGenre(Genre.ACTION);
        movie3.setLikes(2);

        Movie movie4 = new Movie();
        movie4.setId(UUID.randomUUID());
        movie4.setTitle("Comedy Movie 1");
        movie4.setGenre(Genre.COMEDY);
        movie4.setLikes(15);

        Movie movie5 = new Movie();
        movie5.setId(UUID.randomUUID());
        movie5.setTitle("Comedy Movie 2");
        movie5.setGenre(Genre.COMEDY);
        movie5.setLikes(7);

        Movie movie6 = new Movie();
        movie6.setId(UUID.randomUUID());
        movie6.setTitle("Comedy Movie 3");
        movie6.setGenre(Genre.COMEDY);
        movie6.setLikes(4);

        List<Movie> allMovies = Arrays.asList(movie1, movie2, movie3, movie4, movie5, movie6);

        when(movieRepository.findAll()).thenReturn(allMovies);

        Map<Genre, List<Movie>> result = movieService.getTop3LikedMoviesByGenre();

        assertNotNull(result);
        assertTrue(result.containsKey(Genre.ACTION));
        assertTrue(result.containsKey(Genre.COMEDY));

        List<Movie> actionMovies = result.get(Genre.ACTION);
        assertEquals(3, actionMovies.size());
        assertEquals("Action Movie 2", actionMovies.get(0).getTitle()); // Най-лайкваният филм
        assertEquals("Action Movie 1", actionMovies.get(1).getTitle());
        assertEquals("Action Movie 3", actionMovies.get(2).getTitle());

        List<Movie> comedyMovies = result.get(Genre.COMEDY);
        assertEquals(3, comedyMovies.size());
        assertEquals("Comedy Movie 1", comedyMovies.get(0).getTitle()); // Най-лайкваният филм
        assertEquals("Comedy Movie 2", comedyMovies.get(1).getTitle());
        assertEquals("Comedy Movie 3", comedyMovies.get(2).getTitle());

        assertTrue(actionMovies.get(0).getLikes() >= actionMovies.get(1).getLikes());
        assertTrue(actionMovies.get(1).getLikes() >= actionMovies.get(2).getLikes());

        assertTrue(comedyMovies.get(0).getLikes() >= comedyMovies.get(1).getLikes());
        assertTrue(comedyMovies.get(1).getLikes() >= comedyMovies.get(2).getLikes());
    }





}
