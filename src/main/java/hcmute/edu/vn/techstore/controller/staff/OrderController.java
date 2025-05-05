package hcmute.edu.vn.techstore.controller.staff;

import hcmute.edu.vn.techstore.dto.response.OrderResponse;
import hcmute.edu.vn.techstore.service.interfaces.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller("staffOrderController")
@RequiredArgsConstructor
@RequestMapping({"/staff/orders","/admin/orders"})
public class OrderController {
    private final IOrderService orderService;

    @GetMapping("")
    public String getOrders(Model model) {
        List<OrderResponse> orderResponseList = orderService.getAllOrders();
        model.addAttribute("orderResponseList", orderResponseList);
        return "admin/order/order-list";
    }

    @GetMapping("/{id}")
    public String getOrder(@PathVariable("id") Long id, Model model) {
        OrderResponse orderResponse = orderService.getOrderById(id);
        model.addAttribute("orderResponse", orderResponse);
        return "admin/order/order-detail";
    }
}
