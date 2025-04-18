package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.service.interfaces.IProductService;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller("userHomeController")
@RequiredArgsConstructor
public class HomeController {
    private final IUserService userService;
    private final IProductService productService;

    @GetMapping("/home")
    public String home(Model model) {
        return "web/index1";
    }

    @GetMapping("/products")
    public String getAllProducts(@RequestParam Map<String, Object> params,
                                 @RequestParam(defaultValue = "0") int pageNumber,
                                 @RequestParam(defaultValue = "12") int pageSize,
                                 @RequestParam(defaultValue = "asc") String sortOrder,
                                 @RequestParam(defaultValue = "price") String sortBy,
                                 Model model) {

        Sort sort = sortOrder.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<ProductEntity> productPage = productService.findAllProductActive(pageable);
        model.addAttribute("products", productPage.getContent());
        return "web/collection";
    }
}
