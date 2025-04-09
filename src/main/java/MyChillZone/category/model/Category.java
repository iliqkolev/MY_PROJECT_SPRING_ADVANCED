package MyChillZone.category.model;

import MyChillZone.movie.model.Genre;
import jakarta.persistence.*;
import lombok.*;


import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Category {

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
}
