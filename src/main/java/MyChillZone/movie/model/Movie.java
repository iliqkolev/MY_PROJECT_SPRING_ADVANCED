package MyChillZone.movie.model;


import MyChillZone.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;


import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String imageUrl;

    private int likes;

    @ManyToMany(mappedBy = "likedMovies")
    private List<User> likedByUsers;

    @ManyToMany(mappedBy = "favouriteMovies")
    private List<User> favouritedByUsers;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id); // сравняваме по id
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // генерираме хеш стойност въз основа на id
    }
}
