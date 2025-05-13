// src/main/java/hcmute/edu/vn/techstore/service/payment/VnPayPaymentStrategy.java
package hcmute.edu.vn.techstore.service.payment;

import hcmute.edu.vn.techstore.config.VNPAYConfig;
import hcmute.edu.vn.techstore.dto.request.CheckoutRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("VNPAY")
public class VnPayPaymentStrategy implements PaymentStrategy {

    @Override
    public String processPayment(CheckoutRequest checkoutRequest) {
        // Lấy HttpServletRequest và baseUrl
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attrs.getRequest();
        String baseUrl = request.getScheme() + "://"
                + request.getServerName() + ":" + request.getServerPort();

        // Chuyển totalPrice (String) sang số
        long amount = Long.parseLong(
                checkoutRequest.getTotalPrice().replaceAll("[^\\d]", "")
        );

        // 1. Chuẩn bị config & params
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        String txnRef = VNPAYConfig.getRandomNumber(8);
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_IpAddr", VNPAYConfig.getIpAddress(request));
        vnp_Params.put("vnp_TmnCode", VNPAYConfig.vnp_TmnCode);

        vnp_Params.put("vnp_Amount", amount + "00"); // VND *100
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_OrderInfo", "Thanh toán đơn #" + txnRef);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", baseUrl + VNPAYConfig.vnp_Returnurl);

        // Thời gian tạo và hết hạn
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", fmt.format(cld.getTime()));
        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", fmt.format(cld.getTime()));

        // 4. Build query string (URL-encoded)
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String salt = VNPAYConfig.vnp_HashSecret;
        String vnp_SecureHash = VNPAYConfig.hmacSHA512(salt, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPAYConfig.vnp_PayUrl + "?" + queryUrl;
        return paymentUrl;
    }

    public int orderReturn(HttpServletRequest request) {
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = null;
            String fieldValue = null;
            try {
                fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = VNPAYConfig.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }
}