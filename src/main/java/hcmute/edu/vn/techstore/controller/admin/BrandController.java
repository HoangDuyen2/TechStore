package hcmute.edu.vn.techstore.controller.admin;

import hcmute.edu.vn.techstore.dto.request.BrandRequest;
import hcmute.edu.vn.techstore.dto.response.BrandResponse;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import hcmute.edu.vn.techstore.service.IBrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/brands")
public class BrandController {
    @Autowired
    private IBrandService brandService;

    @GetMapping("")
    public String brands(Model model,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size) {
        Page<BrandResponse> brands = brandService.findAll(PageRequest.of(page, size));
        model.addAttribute("brands", brands.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", brands.getTotalPages());
        return "admin/brand/brand-list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        BrandRequest brandRequest = new BrandRequest();
        model.addAttribute("brand", brandRequest);
        return "admin/brand/new-brand";
    }

    @PostMapping("/insert")
    public String insertBrand(@ModelAttribute("brand") BrandRequest brandRequest, @RequestParam("file") MultipartFile file, Model model) throws IOException {
        String msg = "";
        if (!brandRequest.getBrandName().matches("^[a-zA-Z].*")) {
            msg = "Brand Name must start with a letter";
            model.addAttribute("error", msg);
            model.addAttribute("brand", brandRequest);
            return "admin/brand/new-brand";
        }
        if (!brandRequest.getBrandName().matches("^[a-zA-Z0-9\\s]+$")) {
            msg = "Name is not valid";
            model.addAttribute("error", msg);
            return "admin/brand/new-brand";
        }
        if (brandService.findByName(brandRequest.getBrandName()) != null) {
            msg = "Name already exists";
            model.addAttribute("error", msg);
            return "admin/brand/new-brand";
        }
        if (brandService.addBrand(brandRequest, file)) {
            return "redirect:/admin/brands";
        } else {
            msg = "Something went wrong";
            model.addAttribute("error", msg);
        }
        return "admin/brand/new-brand";
    }

    @GetMapping("/edit/{id}")
    public String editBrand(@PathVariable("id") Long id, Model model) {
        BrandRequest brand = brandService.findByIdRequest(id).get();
        model.addAttribute("brandId", id);
        model.addAttribute("brand", brand);
        return "admin/brand/update-brand";
    }

    @PostMapping("/update/{id}")
    public String editBrand(@Valid @ModelAttribute("brand") BrandRequest brandRequest,
                            Model model, @PathVariable("id") Long id,
                            @RequestParam("file") MultipartFile file,
                            BindingResult bindingResult) {
        String msg = "";
        if (bindingResult.hasErrors()) {
            msg = bindingResult.getFieldError().getDefaultMessage();
            model.addAttribute("msg", msg);
            return "admin/brand/brand-list";
        }
        if (!brandRequest.getBrandName().matches("^[a-zA-Z].*")) {
            msg = "Brand Name must start with a letter";
            model.addAttribute("error", msg);
            model.addAttribute("brand", brandRequest);
            return "admin/brand/update-brand";
        }
        if (!brandRequest.getBrandName().matches("^[a-zA-Z0-9\\s]+$")) {
            msg = "Name is not valid";
            model.addAttribute("msg", msg);
            return "admin/brand/update-brand";
        }
        BrandEntity brand = brandService.findByName(brandRequest.getBrandName());
        if (brand != null && !brand.getId().equals(id)) {
            msg = "Brand already exists";
            model.addAttribute("error", msg);
            model.addAttribute("brand", brandRequest);
            return "admin/brand/update-brand";
        }
        if (file == null || file.isEmpty()) {
            if (brandService.updateBrand(brandRequest, id)) {
                return "redirect:/admin/brands";
            }
        } else if (!file.isEmpty()) {
            if (brandService.updateBrand(brandRequest, id, file)) {
                return "redirect:/admin/brands";
            }
        } else {
            msg = "Something went wrong";
            model.addAttribute("msg", msg);
            model.addAttribute("category", brandRequest);
        }
        return "admin/brand/update-brand";
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteBrand(@PathVariable(value = "id") Long id) {
        Map<String, String> response = new HashMap<>();
        if (brandService.deleteBrand(id)) {
            response.put("status", "success");
            response.put("message", "Brand deleted successfully.");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to delete the brand.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
