package hcmute.edu.vn.techstore.controller;

import hcmute.edu.vn.techstore.dto.response.BrandResponse;
import hcmute.edu.vn.techstore.dto.response.CartResponse;
import hcmute.edu.vn.techstore.dto.response.OrderResponse;
import hcmute.edu.vn.techstore.dto.response.UserResponse;
import hcmute.edu.vn.techstore.entity.BrandEntity;
import hcmute.edu.vn.techstore.service.interfaces.IBrandService;
import hcmute.edu.vn.techstore.service.interfaces.ICartService;
import hcmute.edu.vn.techstore.service.interfaces.IOrderService;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
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
            String email = SecurityUtils.getCurrentUsername();
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
}