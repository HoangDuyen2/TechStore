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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductController {

    // Inject any required services here
    private final IBrandService brandService;
    private final IProductService productService;

    @RequestMapping("")
    public String listUser(Model model) {
        List<ProductDTO> products = productService.findAllProduct();

        model.addAttribute("products", products);
        return "admin/product/product-list";
    }

    @GetMapping("/save")
    public String showAddNewProductForm(Model model) {
        model.addAttribute("product", new ProductDTO());
        List<BrandEntity> brands = brandService.findAllByIsActivedTrue();
        model.addAttribute("brands", brands);
        return "admin/product/add-product";
    }

    @GetMapping("/save/{id}")
    public String showAddProductForm(@PathVariable("id") Long id, Model model) {
        ProductDTO product = productService.findProductById(id);
        model.addAttribute("product", product != null ? product : new ProductDTO());
        List<BrandEntity> brands = brandService.findAllByIsActivedTrue();
        model.addAttribute("brands", brands);
        return "admin/product/add-product";
    }

    @PostMapping("/save")
    public String addProduct(@Valid @ModelAttribute("product") ProductDTO product,
                             BindingResult result,
                             @RequestParam("file") MultipartFile file,
                             @RequestParam("existingImagePath") String existingImagePath,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            List<BrandEntity> brands = brandService.findAllByIsActivedTrue();
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

        String message = (product.getId() != null) ? "Chỉnh sửa sản phẩm thành công!" : "Thêm sản phẩm thành công!";
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/admin/products";
    }

}
