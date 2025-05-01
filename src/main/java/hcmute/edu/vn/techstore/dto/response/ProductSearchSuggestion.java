package hcmute.edu.vn.techstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductSearchSuggestion {
    private Long id;
    private String name;
    private String thumbnail;
    private String price;
}