package hcmute.edu.vn.techstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "reviews")
public class ReviewEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 65535)
    private String comment;

    @Column(nullable = false)
    private int rating;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false, referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false, referencedColumnName = "id")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false, referencedColumnName = "id")
    private OrderEntity order;


    @OneToMany(mappedBy = "parentReview", cascade = CascadeType.ALL)
    private List<ReviewEntity> response;

    @ManyToOne
    @JoinColumn(name = "parentReviewId", nullable = true, referencedColumnName = "id")
    private ReviewEntity parentReview; //Đây là review cha
}
