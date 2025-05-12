package hcmute.edu.vn.techstore.service.payment;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import hcmute.edu.vn.techstore.convert.CurrencyConverter;
import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;

import java.math.BigDecimal;
import java.util.List;

public class PaypalPaymentStrategy implements PaymentStrategy {
    private final APIContext apiContext;
    private final CurrencyConverter currencyConverter;
    private final String returnUrl;
    private final String cancelUrl;

    public PaypalPaymentStrategy(APIContext apiContext, CurrencyConverter currencyConverter, String returnUrl, String cancelUrl) {
        this.apiContext = apiContext;
        this.currencyConverter = currencyConverter;
        this.returnUrl = returnUrl;
        this.cancelUrl = cancelUrl;
    }

    @Override
    public String processPayment(CheckoutRequest request) {
        try {
            // Chuyển đổi tiền tệ từ VND sang USD
            BigDecimal vndAmount = new BigDecimal(request.getTotalPrice().replaceAll("[^\\d]", ""));
            BigDecimal usdAmount = currencyConverter.convert(vndAmount);

            // Create payment details
            Amount amount = new Amount();
            amount.setCurrency("USD");
            amount.setTotal(String.format("%.2f", usdAmount));

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription("Payment for order");

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            RedirectUrls urls = new RedirectUrls();
            urls.setReturnUrl(returnUrl);
            urls.setCancelUrl(cancelUrl);

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(List.of(transaction));
            payment.setRedirectUrls(urls);

            // Create PayPal payment
            Payment createdPayment = payment.create(apiContext);

            // Get approval URL
            return createdPayment.getLinks().stream()
                    .filter(link -> "approval_url".equals(link.getRel()))
                    .map(Links::getHref)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No approval URL found"));

        } catch (PayPalRESTException e) {
            throw new RuntimeException("PayPal payment failed: " + e.getMessage());
        }
    }
} 