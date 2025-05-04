package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.dto.response.OrderResponse;
import hcmute.edu.vn.techstore.service.interfaces.IOrderService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller("userController")
@RequiredArgsConstructor
public class UserController {
    private final IOrderService orderService;
    @GetMapping("/my-account")
    public String myAccount(Model model) {
        return "web/profile";
    }

    @GetMapping("/order-history")
    public String manageOrder(Model model) {
        List<OrderResponse> orderResponseList = orderService.getAllOrdersByUserEmail(SecurityUtils.getCurrentUsername());
        model.addAttribute("orderResponseList", orderResponseList);
        return "web/order-history";
    }
}
