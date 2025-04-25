package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.service.interfaces.IBrandService;
import hcmute.edu.vn.techstore.service.interfaces.IProductService;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller("userHomeController")
@RequiredArgsConstructor
public class HomeController {
    private final IUserService userService;
    private final IProductService productService;
    private final IBrandService brandService;

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("productHomeSliders", productService.getProductHomeSlider());
        model.addAttribute("productHomeTrending", productService.getProductHomeTrending());
        model.addAttribute("brands", brandService.findAllByIsActivedTrue());
        return "web/index1";
    }

    @GetMapping("/products")
    public String getAllProducts(@RequestParam Map<String, Object> params,
                                 @RequestParam(defaultValue = "0") int pageNumber,
                                 @RequestParam(defaultValue = "2") int pageSize,
                                 @RequestParam(defaultValue = "asc") String sortOrder,
                                 @RequestParam(defaultValue = "price") String sortBy,
                                 Model model) {

        Sort sort = sortOrder.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<ProductEntity> productPage = productService.filterProducts(params, pageable);

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

}
