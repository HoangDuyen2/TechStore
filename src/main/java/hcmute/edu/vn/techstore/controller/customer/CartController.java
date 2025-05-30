package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.dto.response.CartResponse;
import hcmute.edu.vn.techstore.entity.CartEntity;
import hcmute.edu.vn.techstore.service.interfaces.ICartDetailService;
import hcmute.edu.vn.techstore.service.interfaces.ICartService;
import hcmute.edu.vn.techstore.service.interfaces.IProductService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final ICartService cartService;
    private final ICartDetailService cartDetailService;
    private final IProductService productService;

    @GetMapping("/carts")
    public String cartPage(Model model) {
        model.addAttribute("productHomeTrending", productService.getProductHomeTrending());
        return "web/cart-page";
    }

    @GetMapping("/add-cart")
    @ResponseBody
    public String addCart(@RequestParam("productId") Long productId, Principal principal) {
        cartService.addCart(productId, SecurityUtils.getCurrentUsername());
        return "Thêm sản phẩm vào giỏ hàng thành công!";
    }

    @GetMapping("/decrease-cart")
    @ResponseBody
    public String decreaseCartDetail(@RequestParam("productId") Long productId, Model model) {
        CartEntity cartEntity = cartService.getCartEntity(SecurityUtils.getCurrentUsername());
        cartDetailService.decreaseCartDetail(productId, cartEntity);
        return "Giảm sản phẩm trong giỏ hàng thành công!";
    }

    @GetMapping("/delete-cart")
    @ResponseBody
    public String deleteCartDetail(@RequestParam("productId") Long productId) {
        CartEntity cartEntity = cartService.getCartEntity(SecurityUtils.getCurrentUsername());
        cartDetailService.deleteCartDetail(productId, cartEntity);
        return "Xóa sản phẩm trong giỏ hàng thành công!";
    }
    @GetMapping("/delete-all")
    public String deleteAllCartDetail() {
        cartService.deleteAllCartDetails(SecurityUtils.getCurrentUsername());
        return "redirect:/carts";
    }
}
