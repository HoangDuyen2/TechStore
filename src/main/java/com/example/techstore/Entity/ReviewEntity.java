package com.example.techstore.Entity;

import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "reviewId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 65535)
    private String comment;

    @Column(nullable = false)
    private double rating;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    private OrderEntity order;

    @OneToMany(mappedBy = "review")
    private List<ImageEntity> images;

    @OneToMany(mappedBy = "parentReview", cascade = CascadeType.ALL)
    private List<ReviewEntity> response;

    @ManyToOne
    @JoinColumn(name = "parentReviewId")
    private ReviewEntity parentReview;//Đây là review cha
}
