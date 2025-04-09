package hcmute.edu.vn.techstore.entity;

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
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "warranty")
    private String warranty;

    @Column(name = "batteryCapacity")
    private String batteryCapacity;

    @Column(name = "frontcamera")
    private String frontCamera;

    @Column(name = "rearcamera")
    private String rearCamera;

    @Column(name = "connectivity")
    private String connectivity;

    @Column(name = "stockQuantity")
    private int stockQuantity;

    @Column(name = "operatingSystem")
    private String operatingSystem;

    @Column(name = "processor")
    private String processor;

    @Column(name = "sim")
    private String sim;

    @Column(name = "thumbnail")
    private String thumbnail;

    @ManyToOne
    @JoinColumn(name = "brandId", referencedColumnName = "id")
    private BrandEntity brand;

    @OneToMany(mappedBy = "product")
    private List<ReviewEntity> reviews;

    @OneToMany(mappedBy = "product")
    private List<ImageEntity> images;

    @OneToMany(mappedBy = "product")
    private List<CartDetailEntity> cartDetails;

    @OneToMany(mappedBy = "product")
    private List<OrderDetailEntity> orderDetails;
}
