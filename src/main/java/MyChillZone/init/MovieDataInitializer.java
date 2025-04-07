package MyChillZone.init;

import MyChillZone.movie.model.Genre;
import MyChillZone.movie.model.Movie;
import MyChillZone.movie.model.repository.MovieRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieDataInitializer {

    private final MovieRepository movieRepository;

    @PostConstruct
    public void init() {
        if (movieRepository.count() == 0) {
            List<Movie> movies = List.of(
                    // Action Movies
                    Movie.builder().genre(Genre.ACTION).title("Batman Begins").imageUrl("/images/movie/Batman_Begins.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.ACTION).title("The Dark Knight Rises").imageUrl("/images/movie/The_Dark_Knight_Rises.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.ACTION).title("The Batman").imageUrl("/images/movie/The_Batman.jfif").likes(0).build(),
                    Movie.builder().genre(Genre.ACTION).title("The Equalizer").imageUrl("/images/movie/The_Equalizer.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.ACTION).title("The Equalizer 2").imageUrl("/images/movie/The_Equalizer_2.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.ACTION).title("The Equalizer 3").imageUrl("/images/movie/The_Equalizer_3.png").likes(0).build(),
                    Movie.builder().genre(Genre.ACTION).title("John Wick").imageUrl("/images/movie/John_Wick_.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.ACTION).title("John Wick 2").imageUrl("/images/movie/John_Wick_2.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.ACTION).title("John Wick 3").imageUrl("/images/movie/John_Wick_3.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.ACTION).title("John Wick 4").imageUrl("/images/movie/John_Wick_4.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.ACTION).title("The Mechanic").imageUrl("/images/movie/The_Mechanic.jfif").likes(0).build(),

                    // Comedy Movies
                    Movie.builder().genre(Genre.COMEDY).title("Sam vkyshti").imageUrl("/images/movie/Sam-vkyshti.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.COMEDY).title("Sam vkashti 2").imageUrl("/images/movie/sam-vkashti-2.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.COMEDY).title("Sam vkashti 3").imageUrl("/images/movie/sam-vkashti-3.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.COMEDY).title("Sam vkashti 4").imageUrl("/images/movie/sam-vkashti-4.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.COMEDY).title("Vnedreni v chas").imageUrl("/images/movie/vnedreni_v_chas.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.COMEDY).title("Vnedreni v chas 2").imageUrl("/images/movie/vnedreni-v-chas2.jpg" ).likes(0).build(),
                    Movie.builder().genre(Genre.COMEDY).title("Ergenski zapoi").imageUrl("/images/movie/ergenski.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.COMEDY).title("Ergenski zapoi 2").imageUrl("/images/movie/ergenski2.jfif").likes(0).build(),
                    Movie.builder().genre(Genre.COMEDY).title("Ergenski zapoi 3").imageUrl("/images/movie/ergenski3.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.COMEDY).title("Ergenski zapoi 4").imageUrl("/images/movie/ergenksi4.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.COMEDY).title("Agent XXL").imageUrl("/images/movie/agent-xxl.jpg").likes(0).build(),

                    // Horror Movies
                    Movie.builder().genre(Genre.HORROR).title("The Exorcist").imageUrl("/images/movie/The_exorcist.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.HORROR).title("The Shining").imageUrl("/images/movie/The_Shining.jfif").likes(0).build(),
                    Movie.builder().genre(Genre.HORROR).title("The Halloween").imageUrl("/images/movie/The_Halloween.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.HORROR).title("A Nightmare on Elm Street").imageUrl("/images/movie/A%20Nightmare%20on%20Elm%20Street.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.HORROR).title("The ring").imageUrl("/images/movie/The_ring.jfif").likes(0).build(),
                    Movie.builder().genre(Genre.HORROR).title("A quiet place").imageUrl("/images/movie/A_quiet_place.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.HORROR).title("Hereditary").imageUrl("/images/movie/hereditary.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.HORROR).title("Midsommar").imageUrl("/images/movie/Midsommar.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.HORROR).title("Anabel").imageUrl("/images/movie/Anabel.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.HORROR).title("Conjurning").imageUrl("/images/movie/conjurning.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.HORROR).title("Conjurning 2").imageUrl("/images/movie/conjurning_2.jpg").likes(0).build(),

                    // Fantasy Movies
                    Movie.builder().genre(Genre.FANTASY).title("The Lord of the Rings").imageUrl("/images/movie/the_lord_of_the_rings.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.FANTASY).title("Harry Potter").imageUrl("/images/movie/Harry.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.FANTASY).title("Game of Thrones").imageUrl("/images/movie/game_of_thrones.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.FANTASY).title("The Never Ending Story").imageUrl("/images/movie/The%20NeverEnding%20Story.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.FANTASY).title("Cloud Atlas").imageUrl("/images/movie/cloud_atlas.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.FANTASY).title("A quiet place").imageUrl("/images/movie/A_quiet_place.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.FANTASY).title("Dark Shadows").imageUrl("/images/movie/dark_shadows.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.FANTASY).title("Midsommar").imageUrl("/images/movie/Midsommar.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.FANTASY).title("How to Train Your Dragon").imageUrl("/images/movie/dragonjpg.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.FANTASY).title("Beauty and The Beast").imageUrl("/images/movie/beauty.jpg").likes(0).build(),
                    Movie.builder().genre(Genre.FANTASY).title("Dune").imageUrl("/images/movie/Dune_(2021_film).jpg").likes(0).build()

            );

            movieRepository.saveAll(movies);
        }
    }



}
