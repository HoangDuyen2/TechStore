package hcmute.edu.vn.techstore.controller.admin;



import hcmute.edu.vn.techstore.dto.ProductDTO;
import hcmute.edu.vn.techstore.dto.ProductImageUpdateDTO;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import hcmute.edu.vn.techstore.service.interfaces.IBrandService;
import hcmute.edu.vn.techstore.service.interfaces.IImageService;
import hcmute.edu.vn.techstore.service.interfaces.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductController {

    // Inject any required services here
    private final IBrandService brandService;
    private final IProductService productService;
    private final IImageService imageService;


    @RequestMapping("")
    public String ListProduct(Model model) {
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

    @GetMapping("/images/{productId}")
    public String showProductImages(@PathVariable Long productId, Model model) {
        ProductDTO product = productService.findProductById(productId);
        ProductImageUpdateDTO form = new ProductImageUpdateDTO();
        form.setProductId(productId);

        // load existing paths
        List<String> paths = imageService.getImagePathsByProductId(productId);
        form.setExistingImagePaths(paths);
        form.setKeptImagePaths(new ArrayList<>(paths));

        model.addAttribute("product", product);
        model.addAttribute("form", form);
        return "admin/product/product-images";
    }

    @PostMapping("/images/{productId}/save")
    public String saveProductImages(@PathVariable Long productId,
                                    @ModelAttribute("form") ProductImageUpdateDTO form,
                                    RedirectAttributes ra) {

        try {
            imageService.updateProductImages(form);
            ra.addFlashAttribute("successMessage", "Cập nhật ảnh thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/products/images/" + productId;
    }
}
