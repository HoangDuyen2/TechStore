package com.example.techstore.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "products")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Sử dụng bảng chung cho tất cả các loại sản phẩm
@DiscriminatorColumn(name = "product_type", discriminatorType = DiscriminatorType.STRING) // Cột phân biệt loại sản phẩm
public abstract class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productId")
    private long productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "warrantyPeriod")
    private String warrantyPeriod;

    @ManyToOne
    @JoinColumn(name = "brandId", referencedColumnName = "brandId")
    private BrandEntity brand;

    @ManyToOne
    @JoinColumn(name = "reviewId", referencedColumnName = "reviewId")
    private ReviewEntity review;

    @OneToMany(mappedBy = "product")
    private List<ImageEntity> image;

}
