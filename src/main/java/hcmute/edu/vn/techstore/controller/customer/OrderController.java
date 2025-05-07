package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.Enum.EPayment;
import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.service.interfaces.IOrderService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller("userOrderController")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @GetMapping("/checkout")
    public String orderPage(Model model, @RequestParam(value = "selectedProductIds", required = false) List<Long> selectedProductIds) {
        model.addAttribute("checkoutRequest", orderService.getCheckoutRequest(SecurityUtils.getCurrentUsername(), selectedProductIds));
        model.addAttribute("paymentMethods", EPayment.values());
        return "web/checkout-style1";
    }

    @PostMapping("/apply-discount")
    public String applyDiscount(@ModelAttribute("checkoutRequest") CheckoutRequest checkoutRequest, Model model) {
        checkoutRequest = orderService.applyDiscount(checkoutRequest);
        if (checkoutRequest.getDiscountValue() == null) {
            model.addAttribute("error", "Invalid discount code");
        } else {
            model.addAttribute("success", "Discount applied successfully");
        }
        model.addAttribute("checkoutRequest", checkoutRequest);
        model.addAttribute("paymentMethods", EPayment.values());
        return "web/checkout-style1";
    }

    @PostMapping("/create-order")
    public String createOrder(@ModelAttribute("checkoutRequest") CheckoutRequest checkoutRequest,
                              @RequestParam("paymentMethod") String paymentMethod,
                              Model model) {
        checkoutRequest.setPaymentMethod(EPayment.valueOf(paymentMethod));

        try {
            Long orderId = orderService.createOrder(checkoutRequest);
            return "redirect:/order-complete?orderId=" + orderId;
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create order: " + e.getMessage());
            model.addAttribute("checkoutRequest", checkoutRequest);
            model.addAttribute("paymentMethods", EPayment.values());
            return "web/checkout-style1";
        }
    }

    @GetMapping("/order-complete")
    public String orderComplete(@RequestParam Long orderId, Model model) {
        model.addAttribute("orderCompleteResponse", orderService.getOrderCompleteResponse(orderId));
        model.addAttribute("message", "Order completed successfully!");
        return "web/order-complete";
    }

    @GetMapping("/orders/{orderId}/cancel")
    public String cancelOrder(Model model, @PathVariable Long orderId) {
        orderService.changeStatusOrder(orderId, EOrderStatus.CANCELLED);
        return "redirect:/order-history";
    }

    @GetMapping("/orders/{orderId}/received")
    public String receivedOrder(Model model, @PathVariable Long orderId) {
        orderService.changeStatusOrder(orderId, EOrderStatus.DELIVERED_SUCCESSFULLY);
        return "redirect:/order-history";
    }

    @PostMapping("/orders/{id}/change-address")
    @ResponseBody
    public ResponseEntity<?> changeOrderAddress(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newAddress = body.get("address");
        orderService.updateOrderAddress(id, newAddress);
        return ResponseEntity.ok().build();
    }
}
