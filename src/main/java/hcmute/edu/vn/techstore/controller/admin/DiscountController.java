package hcmute.edu.vn.techstore.controller.admin;

import hcmute.edu.vn.techstore.entity.DiscountEntity;
import hcmute.edu.vn.techstore.dto.request.DiscountRequest;
import hcmute.edu.vn.techstore.dto.response.DiscountResponse;
import hcmute.edu.vn.techstore.service.interfaces.IDiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/discounts")
@RequiredArgsConstructor
public class DiscountController {
    private final IDiscountService discountService;

    @GetMapping("")
    public String showDiscounts(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        Page<DiscountResponse> discountResponses = discountService.findAll(PageRequest.of(page, size));
        model.addAttribute("discounts", discountResponses.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", discountResponses.getTotalPages());
        return "admin/discount/discount-list";
    }

    @GetMapping("/add")
    public String addDiscount(Model model) {
        DiscountRequest discountRequest = new DiscountRequest();
        model.addAttribute("discount", discountRequest);
        return "admin/discount/new-discount";
    }

    @PostMapping("/insert")
    public String insertDiscount(Model model,
                                 @Valid @ModelAttribute("discount") DiscountRequest discountRequest,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Validation failed. Please correct the errors and try again.");
            return "admin/discount/new-discount";
        }

        String msg = "";
        DiscountEntity existedDiscount = discountService.findByCode(discountRequest.getCode());
        if (existedDiscount != null) {
            model.addAttribute("error", "The discount code already exists!");
            return "admin/discount/new-discount";
        }

        if (discountService.addDiscount(discountRequest)) {
            return "redirect:/admin/discounts";
        } else {
            msg = "Failed to add discount";
            model.addAttribute("error", msg);
            return "admin/discount/new-discount";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditDiscountForm(@PathVariable("id") Long id, Model model) {
        DiscountRequest discountRequest = discountService.findByIdRequest(id);
        model.addAttribute("discount", discountRequest);
        model.addAttribute("discountId", id);
        return "admin/discount/update-discount";
    }

    @PostMapping("/update/{id}")
    public String updateDiscount(@PathVariable("id") Long id,
                                  @Valid @ModelAttribute("discount") DiscountRequest discountRequest,
                                  BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Validation failed. Please correct the errors and try again.");
            model.addAttribute("discountId", id);
            return "admin/discount/update-discount";
        }

        if (discountService.updateDiscount(id, discountRequest)) {
            return "redirect:/admin/discounts";
        } else {
            model.addAttribute("error", "Discount not found");
            return "admin/discount/update-discount";
        }
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteDiscount(@PathVariable(value = "id") Long id) {
        Map<String, String> response = new HashMap<>();
        if (discountService.deleteDiscount(id)) {
            response.put("status", "success");
            response.put("message", "Discount deleted successfully.");
            return org.springframework.http.ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to delete the discount.");
            return org.springframework.http.ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
