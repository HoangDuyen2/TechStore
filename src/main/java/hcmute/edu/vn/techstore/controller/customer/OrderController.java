package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.Enum.EPayment;
import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.service.interfaces.IOrderService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller("userOrderController")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @GetMapping("/checkout")
    public String orderPage(Model model) {
        model.addAttribute("checkoutRequest", orderService.getCheckoutRequest(SecurityUtils.getCurrentUsername()));
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
            orderService.createOrder(checkoutRequest);
            return "redirect:/order-complete";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create order: " + e.getMessage());
            model.addAttribute("checkoutRequest", checkoutRequest);
            model.addAttribute("paymentMethods", EPayment.values());
            return "web/checkout-style1";
        }
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
}
