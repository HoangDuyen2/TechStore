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
@Table(name = "images")
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imageId")
    private Long id;

    @Column(name = "img_path", length = 500)
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "reviewId", referencedColumnName = "reviewId")
    private ReviewEntity review;
}
