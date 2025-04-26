package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.dto.response.CartResponse;
import hcmute.edu.vn.techstore.dto.response.UserResponse;
import hcmute.edu.vn.techstore.service.interfaces.ICartService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final ICartService cartService;

    @GetMapping("/carts")
    public String cartPage(Model model) {
        return "web/cart-page";
    }

    @GetMapping("/add-cart")
    public String addCart(Model model) {
        cartService.addCart((CartResponse) model.getAttribute("cart"), SecurityUtils.getCurrentUsername());
        return "web/index1";
    }
}
