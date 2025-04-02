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
@Table(name = "brands")
public class BrandEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brandId")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToOne
    @JoinColumn(name = "imageId", referencedColumnName = "imageId")
    private ImageEntity img;

    @OneToMany(mappedBy = "brand")
    private List<ProductEntity> products;
}
