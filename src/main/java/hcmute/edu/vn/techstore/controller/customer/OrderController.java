package hcmute.edu.vn.techstore.controller.customer;

import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.Enum.EPayment;
import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import hcmute.edu.vn.techstore.service.interfaces.IOrderService;
import hcmute.edu.vn.techstore.service.payment.PaymentStrategy;
import hcmute.edu.vn.techstore.service.payment.PaymentStrategyFactory;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller("userOrderController")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final PaymentStrategyFactory paymentStrategyFactory;

    @GetMapping("/checkout")
    public String orderPage(Model model, @RequestParam(value = "selectedProductIds", required = false) List<Long> selectedProductIds) {
        model.addAttribute("checkoutRequest", orderService.getCheckoutRequest(SecurityUtils.getCurrentUsername(), selectedProductIds));
        model.addAttribute("paymentMethods", EPayment.values());
        return "web/checkout-style1";
    }

    @PostMapping("/apply-discount")
    public String applyDiscount(@ModelAttribute("checkoutRequest") CheckoutRequest checkoutRequest, Model model) {
        if (checkoutRequest.getDiscounts() != null) {
            for (CheckoutRequest.DiscountCheckout discount : checkoutRequest.getDiscounts()) {
                if (discount.getDiscountCode().equals(checkoutRequest.getDiscountCode())) {
                    model.addAttribute("error", "Discount code already applied");
                    model.addAttribute("checkoutRequest", checkoutRequest);
                    model.addAttribute("paymentMethods", EPayment.values());
                    return "web/checkout-style1";
                }
            }
        }
        checkoutRequest = orderService.applyDiscount(checkoutRequest);
        if (checkoutRequest.getDiscounts() == null) {
            model.addAttribute("error", "Invalid discount code");
        } else {
            if (checkoutRequest.getDiscounts().getLast().getDiscountValue() == null) {
                model.addAttribute("error", "Invalid discount code");
            } else {
                model.addAttribute("success", "Discount applied successfully");
            }
        }
        model.addAttribute("checkoutRequest", checkoutRequest);
        model.addAttribute("paymentMethods", EPayment.values());
        return "web/checkout-style1";
    }

    @PostMapping("/create-order")
    public String createOrder(@ModelAttribute("checkoutRequest") CheckoutRequest checkoutRequest,
                              @RequestParam("paymentMethod") String paymentMethod,
                              Model model, HttpSession session) {
        checkoutRequest.setPaymentMethod(EPayment.valueOf(paymentMethod));

        try {
            // Get the appropriate payment strategy
            PaymentStrategy paymentStrategy = paymentStrategyFactory.getStrategy(checkoutRequest.getPaymentMethod());
            
            // Process payment
            String paymentResult = paymentStrategy.processPayment(checkoutRequest);
            
            if (checkoutRequest.getPaymentMethod() == EPayment.Paypal) {
                session.setAttribute("checkoutRequest", checkoutRequest);
            }

            if (paymentResult.equals("success")) {
                // For non-redirect payment methods (COD, Pay in Store)
                Long orderId = orderService.createOrder(checkoutRequest);
                return "redirect:/order-complete?orderId=" + orderId;
            } else {
                // For redirect payment methods (PayPal, VNPay)
                return "redirect:" + paymentResult;
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create order: " + e.getMessage());
            model.addAttribute("checkoutRequest", checkoutRequest);
            model.addAttribute("paymentMethods", EPayment.values());
            return "web/checkout-style1";
        }
    }

    @GetMapping("/paypal/success")
    public String handlePayPalSuccess(@RequestParam("paymentId") String paymentId,
                                    @RequestParam("PayerID") String payerId,
                                    HttpSession session) {
        try {
            CheckoutRequest checkoutRequest = (CheckoutRequest) session.getAttribute("checkoutRequest");
            if (checkoutRequest == null) {
                return "redirect:/checkout?error=Session expired or invalid";
            }
            Long orderId = orderService.createOrder(checkoutRequest);
            session.removeAttribute("checkoutRequest");
            return "redirect:/order-complete?orderId=" + orderId;
        } catch (Exception e) {
            return "redirect:/checkout?error=" + e.getMessage();
        }
    }

    @GetMapping("/paypal/cancel")
    public String handlePayPalCancel() {
        return "redirect:/checkout?error=Payment cancelled";
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
