package hcmute.edu.vn.techstore.api;

import hcmute.edu.vn.techstore.dto.response.ProductSearchSuggestion;
import hcmute.edu.vn.techstore.service.interfaces.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {
    private final IProductService productService;

    @GetMapping("/search")
    public List<ProductSearchSuggestion> searchProducts(@RequestParam("keyword") String keyword) {
        return productService.searchSuggestions(keyword);
    }
}