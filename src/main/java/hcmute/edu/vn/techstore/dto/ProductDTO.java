package hcmute.edu.vn.techstore.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private String warranty;

    @NotBlank(message = "Battery capacity cannot be blank")
    @Pattern(regexp = "^[0-9]+(mAh)$", message = "Battery capacity should be a number followed by 'mAh' (e.g., 5000mAh)")
    private String batteryCapacity;

    @Pattern(regexp = "^\\d+ MP$", message = "Front camera should be in format like '12 MP'")
    private String frontCamera;

    @Pattern(regexp = "^\\d+ MP$", message = "Rear camera should be in format like '48 MP'")
    private String rearCamera;

    @NotBlank(message = "Connectivity cannot be blank")
    private String connectivity;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQuantity;

    @NotBlank(message = "Operating system cannot be blank")
    private String operatingSystem;

    @NotBlank(message = "Processor cannot be blank")
    private String processor;

    @NotBlank(message = "SIM type cannot be blank")
    private String sim;

    private String thumbnail;

    @NotNull(message = "Brand ID cannot be null")
    private Long brandId;
}