package hcmute.edu.vn.techstore.service.payment;

import com.paypal.base.rest.APIContext;
import hcmute.edu.vn.techstore.Enum.EPayment;
import hcmute.edu.vn.techstore.convert.CurrencyConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStrategyFactory {
    private final APIContext apiContext;
    private final CurrencyConverter currencyConverter;
    private final ApplicationContext applicationContext;

    public PaymentStrategy getStrategy(EPayment method) {
        try {
            return switch (method) {
                case Pay_In_Store -> {
                    yield new PayInStorePaymentStrategy();
                }
                case Cod -> {
                    yield new CodPaymentStrategy();
                }
                case VNPay -> {
                    yield new VnPayPaymentStrategy();
                }
                case Paypal -> {
                    String returnUrl = applicationContext.getBean("paypalReturnUrl", String.class);
                    String cancelUrl = applicationContext.getBean("paypalCancelUrl", String.class);
                    yield new PaypalPaymentStrategy(apiContext, currencyConverter, returnUrl, cancelUrl);
                }
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to create payment strategy", e);
        }
    }
} 