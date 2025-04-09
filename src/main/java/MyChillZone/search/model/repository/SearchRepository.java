package MyChillZone.search.model.repository;

import MyChillZone.movie.model.Genre;
import MyChillZone.movie.model.Movie;
import MyChillZone.search.model.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface SearchRepository extends JpaRepository<Search, UUID> {

    Optional<Search> findByTitleAndGenre(String title, Genre genre);

}
