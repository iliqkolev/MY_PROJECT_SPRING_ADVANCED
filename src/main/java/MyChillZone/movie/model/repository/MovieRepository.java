package MyChillZone.movie.model.repository;

import MyChillZone.movie.model.Genre;
import MyChillZone.movie.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {

    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByGenre(Genre genre);


}
