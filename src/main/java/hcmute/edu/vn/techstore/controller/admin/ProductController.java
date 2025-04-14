package hcmute.edu.vn.techstore.controller.admin;


import hcmute.edu.vn.techstore.dto.ProductDTO;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import hcmute.edu.vn.techstore.service.interfaces.IBrandService;
import hcmute.edu.vn.techstore.service.interfaces.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductController {

    // Inject any required services here
    private final IBrandService brandService;
    private final IProductService productService;

    @RequestMapping("")
    public String index() {
        return "admin/product/product-list";
    }

    @GetMapping("/save")
    public String showAddNewProductForm(Model model) {
        model.addAttribute("product", new ProductDTO());
        List<BrandEntity> brands = brandService.findAll();
        model.addAttribute("brands", brands);
        return "admin/product/add-product";
    }

    @GetMapping("/save/{id}")
    public String showAddProductForm(@PathVariable("id") Long id, Model model) {
        ProductDTO product = productService.findProductById(id);
        model.addAttribute("product", product != null ? product : new ProductDTO());
        List<BrandEntity> brands = brandService.findAll();
        model.addAttribute("brands", brands);
        return "admin/product/add-product";
    }

    @PostMapping("/save")
    public String addProduct(@Valid @ModelAttribute("product") ProductDTO product,
                             BindingResult result,
                             @RequestParam("file") MultipartFile file,
                             @RequestParam("existingImagePath") String existingImagePath,
                             Model model) {

        if (result.hasErrors()) {
            List<BrandEntity> brands = brandService.findAll();
            model.addAttribute("brands", brands);
            return "admin/product/add-product";
        }

        if ((file == null || file.isEmpty()) && existingImagePath != null) {
            product.setThumbnail(existingImagePath);
        }

        try {
            productService.saveProduct(product, file, existingImagePath);
        } catch (IllegalArgumentException e) {
            model.addAttribute("msg", e.getMessage());
            return "admin/product/add-product";
        } catch (RuntimeException e) {
            model.addAttribute("msg", "An error occurred while saving the product: " + e.getMessage());
            return "admin/product/add-product";
        }
        return "redirect:/admin/products";
    }

}
