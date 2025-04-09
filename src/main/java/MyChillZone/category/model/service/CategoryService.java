package MyChillZone.category.model.service;

import MyChillZone.category.model.Category;
import MyChillZone.category.model.repository.CategoryRepository;
import MyChillZone.movie.model.Genre;
import MyChillZone.movie.model.Movie;
import MyChillZone.movie.model.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final MovieRepository movieRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, MovieRepository movieRepository) {
        this.categoryRepository = categoryRepository;
        this.movieRepository = movieRepository;
    }


    public List<Movie> findMoviesByCategory(Genre genre) {

        List<Movie> searchResult = movieRepository.findByGenre(genre);

        if (!searchResult.isEmpty()){
            for (Movie movie : searchResult) {
                Optional<Category> optionalCategory = categoryRepository.findByGenre(genre);
                if (optionalCategory.isEmpty()){
                    Category category = Category.builder().build();
                    category.setGenre(movie.getGenre());
                    category.setTitle(movie.getTitle());
                    category.setImageUrl(movie.getImageUrl());
                    categoryRepository.save(category);
                }
            }
        }

        return searchResult;
    }
}
