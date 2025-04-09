package hcmute.edu.vn.techstore.controller.admin;

import hcmute.edu.vn.techstore.dto.BrandResponse;
import hcmute.edu.vn.techstore.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
}
