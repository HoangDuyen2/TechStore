package hcmute.edu.vn.techstore.controller.admin;

import hcmute.edu.vn.techstore.dto.request.BrandRequest;
import hcmute.edu.vn.techstore.dto.response.BrandResponse;
import hcmute.edu.vn.techstore.service.IBrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
        return "admin/brands/add-brand";
    }
}
