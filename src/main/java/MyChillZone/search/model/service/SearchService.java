package MyChillZone.search.model.service;

import MyChillZone.movie.model.Movie;
import MyChillZone.movie.model.repository.MovieRepository;
import MyChillZone.movie.model.service.MovieService;
import MyChillZone.search.model.Search;
import MyChillZone.search.model.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SearchService {
    private final SearchRepository searchRepository;
    private final MovieRepository movieRepository;

    @Autowired
    public SearchService(SearchRepository searchRepository, MovieRepository movieRepository) {
        this.searchRepository = searchRepository;
        this.movieRepository = movieRepository;
    }


    public List<Movie> searchMovie(String title) {

        List<Movie> searchResult = movieRepository.findByTitleContainingIgnoreCase(title);

        if (!searchResult.isEmpty()){
            for (Movie movie : searchResult) {
                Optional<Search> existingSearch = searchRepository.findByTitleAndGenre(movie.getTitle(), movie.getGenre());
                if (existingSearch.isEmpty()){
                    Search newSearch = Search.builder().build();
                    newSearch.setTitle(movie.getTitle());
                    newSearch.setGenre(movie.getGenre());
                    newSearch.setImageUrl(movie.getImageUrl());

                    searchRepository.save(newSearch);
                }
            }
        }

        return searchResult;
    }





}
