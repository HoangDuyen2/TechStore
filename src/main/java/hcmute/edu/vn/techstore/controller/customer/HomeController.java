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
}
