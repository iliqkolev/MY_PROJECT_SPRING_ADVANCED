package MyChillZone.category.model.repository;

import MyChillZone.category.model.Category;
import MyChillZone.movie.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByGenre(Genre genre);

}
