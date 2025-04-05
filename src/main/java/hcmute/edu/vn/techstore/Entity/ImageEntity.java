package hcmute.edu.vn.techstore.Entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "images")
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "img_path", length = 500)
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "id")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "reviewId", referencedColumnName = "id")
    private ReviewEntity review;

    @OneToOne(mappedBy = "image")
    private UserEntity user;

    @OneToOne(mappedBy = "image")
    private BrandEntity brand;
}
