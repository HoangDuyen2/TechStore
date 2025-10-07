package hcmute.edu.vn.techstore.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompareProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String warranty;
    private String batteryCapacity;
    private String frontCamera;
    private String rearCamera;
    private String connectivity;
    private int stockQuantity;
    private String operatingSystem;
    private String processor;
    private String sim;
    private String thumbnail;
    private Integer star;
    private Long numberOfReviews;
    private String brandName;
    private String brandImage;
}