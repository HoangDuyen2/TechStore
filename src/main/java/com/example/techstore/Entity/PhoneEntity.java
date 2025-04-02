package com.example.techstore.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@DiscriminatorValue("PHONE")
public class PhoneEntity extends ProductEntity {
    @Column(name = "batteryCapacity")
    private String batteryCapacity;

    @Column(name = "fontcamera")
    private String fontCamera;

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
}
