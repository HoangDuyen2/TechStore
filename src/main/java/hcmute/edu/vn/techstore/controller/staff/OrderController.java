package hcmute.edu.vn.techstore.controller.staff;

import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.dto.response.OrderResponse;
import hcmute.edu.vn.techstore.service.interfaces.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String getOrder(@PathVariable("id") Long id, Model model,  @RequestParam(name = "status", required = false, defaultValue = "false") boolean status) {
        OrderResponse orderResponse = orderService.getOrderById(id);
        model.addAttribute("status",status);
        model.addAttribute("orderResponse", orderResponse);
        model.addAttribute("orderStatus", EOrderStatus.values());
        return "admin/order/order-detail";
    }

    @PostMapping("/update-status")
    @ResponseBody
    public ResponseEntity<?> changeOrderStatus(@RequestBody OrderResponse orderResponse ) {
        orderService.changeStatusOrder(orderResponse.getId(), orderResponse.getOrderStatus());
        return ResponseEntity.ok().build();
    }
}
