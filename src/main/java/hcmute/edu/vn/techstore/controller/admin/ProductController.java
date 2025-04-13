package hcmute.edu.vn.techstore.controller.admin;


import hcmute.edu.vn.techstore.dto.ProductDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    @RequestMapping("")
    public String index() {
        return "admin/product-list";
    }

    @GetMapping("/save")
    public String showAddNewProductForm(Model model) {
        model.addAttribute("product", new ProductDTO());
        return "admin/add-product";
    }

}
