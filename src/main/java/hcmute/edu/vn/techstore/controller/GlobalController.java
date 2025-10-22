package hcmute.edu.vn.techstore.controller;

import hcmute.edu.vn.techstore.dto.response.BrandResponse;
import hcmute.edu.vn.techstore.dto.response.CartResponse;
import hcmute.edu.vn.techstore.dto.response.OrderResponse;
import hcmute.edu.vn.techstore.dto.response.UserResponse;
import hcmute.edu.vn.techstore.service.interfaces.IBrandService;
import hcmute.edu.vn.techstore.service.interfaces.ICartService;
import hcmute.edu.vn.techstore.service.interfaces.IOrderService;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.thymeleaf.exceptions.TemplateInputException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalController {

    private final IUserService userService;
    private final ICartService cartService;
    private final IOrderService orderService;
    private final IBrandService brandService;

    @ModelAttribute("user")
    public UserResponse getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = SecurityUtils.getCurrentUsername();
            return userService.getUserByEmail(email);
        }
        return null;
    }

    @ModelAttribute("cart")
    public CartResponse getCarts(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isCustomer = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_CUSTOMER"));

            if (isCustomer) {
                String email = SecurityUtils.getCurrentUsername();
                return cartService.getCart(email);
            }
        }
        return null;
    }

    @ModelAttribute("orderResponseList")
    public List<OrderResponse> getAllOrders(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            List<OrderResponse> orderResponseList = orderService.getAllOrdersByUserEmail(SecurityUtils.getCurrentUsername());
            return orderResponseList;
        }
        return null;
    }

    @ModelAttribute("brands")
    public List<BrandResponse> getAllBrands() {
        List<BrandResponse> brandResponses = brandService.getAllByIsActivedTrue();
        return brandResponses;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(HttpServletRequest request, AccessDeniedException ex) {
        // Kiểm tra nếu request là reset-password, redirect về trang login
        if (request.getRequestURI().contains("/reset-password")) {
            return "redirect:/login?error=access_denied";
        } else {
            return "redirect:/login?error=access_denied";
        }
    }

    @ExceptionHandler(TemplateInputException.class)
    public String handleTemplateInputException(HttpServletRequest request, TemplateInputException ex) {
        // Redirect to login page if it's a reset-password related error
        if (request.getRequestURI().contains("/reset-password")) {
            return "redirect:/login?error=template_error";
        } else {
            return "redirect:/home?error=template_error";
        }
    }
}