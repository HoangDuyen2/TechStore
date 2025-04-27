package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.dto.response.CartResponse;
import hcmute.edu.vn.techstore.dto.response.UserResponse;
import hcmute.edu.vn.techstore.entity.CartEntity;
import hcmute.edu.vn.techstore.service.interfaces.ICartDetailService;
import hcmute.edu.vn.techstore.service.interfaces.ICartService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final ICartService cartService;
    private final ICartDetailService cartDetailService;

    @GetMapping("/carts")
    public String cartPage(Model model) {
        CartResponse cartResponse = cartService.getAllCartDetailInactive(SecurityUtils.getCurrentUsername());
        if (cartResponse != null) {
            model.addAttribute("cartDetailsInActived", cartResponse.getCartDetails());
        }
        return "web/cart-page";
    }

    @GetMapping("/add-cart")
    @ResponseBody
    public String addCart(@RequestParam("productId") Long productId) {
        cartService.addCart(productId, SecurityUtils.getCurrentUsername());
        return "Thêm sản phẩm vào giỏ hàng thành công!";
    }

    @GetMapping("/decrease-cart")
    @ResponseBody
    public String decreaseCartDetail(@RequestParam("productId") Long productId, Model model) {
        Long cartId = cartService.getCartId(SecurityUtils.getCurrentUsername());
        cartDetailService.decreaseCartDetail(productId, cartId);
        return "Giảm sản phẩm trong giỏ hàng thành công!";
    }

    @GetMapping("/delete-cart")
    @ResponseBody
    public String deleteCartDetail(@RequestParam("productId") Long productId) {
        Long cartId = cartService.getCartId(SecurityUtils.getCurrentUsername());
        cartDetailService.deleteCartDetail(productId, cartId);
        return "Xóa sản phẩm trong giỏ hàng thành công!";
    }
    @GetMapping("/delete-all")
    public String deleteAllCartDetail() {
        cartService.deleteAllCartDetails(SecurityUtils.getCurrentUsername());
        return "redirect:/carts";
    }
}
