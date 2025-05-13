package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.dto.ProductImageUpdateDTO;
import hcmute.edu.vn.techstore.dto.response.ProductCollectionResponse;
import hcmute.edu.vn.techstore.dto.response.ProductResponse;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.service.interfaces.IBrandService;
import hcmute.edu.vn.techstore.service.interfaces.IImageService;
import hcmute.edu.vn.techstore.service.interfaces.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller("userProductController")
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final IProductService productService;
    private final IImageService imageService;
    private final IBrandService brandService;

    @GetMapping("")
    public String getAllProducts(@RequestParam Map<String, Object> params,
                                 @RequestParam(defaultValue = "0") int pageNumber,
                                 @RequestParam(defaultValue = "9") int pageSize,
                                 @RequestParam(defaultValue = "asc") String sortOrder,
                                 @RequestParam(defaultValue = "price") String sortBy,
                                 Model model) {

        Sort sort = sortOrder.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<ProductCollectionResponse> productPage = productService.filterProducts(params, pageable);

        // Tạo baseUrl giữ filter
        String queryParams = params.entrySet().stream()
                .filter(entry -> !List.of("pageNumber", "pageSize", "sortBy", "sortOrder").contains(entry.getKey()))
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        String baseUrl = "/products" + (queryParams.isEmpty() ? "?" : "?" + queryParams + "&");

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("brands", brandService.findAllByIsActivedTrue());
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        return "web/collection";
    }



    @GetMapping("/{id}")
    public String getProductId(@PathVariable("id") Long id, Model model) {
        prepareProductModel(id, model);
        return "web/product-template7";
    }
    private void prepareProductModel(Long id, Model model) {
        ProductResponse productResponse = productService.findProductResponseById(id).orElse(null);
        ProductImageUpdateDTO productImageUpdateDTO = new ProductImageUpdateDTO();
        productImageUpdateDTO.setExistingImagePaths(imageService.getImagePathsByProductId(id));
        model.addAttribute("productHomeTrending", productService.getProductHomeTrending());
        model.addAttribute("productImages", productImageUpdateDTO);
        model.addAttribute("product", productResponse);
    }

    @GetMapping("/quickview/{id}")
    public String getQuickView(@PathVariable("id") Long id, Model model) {
        prepareProductModel(id, model);
        return "web/fragments/quickview-slider :: quickview"; // chỉ phần cần render
    }
}
