// src/main/java/hcmute/edu/vn/techstore/config/VNPAYConfig.java
package hcmute.edu.vn.techstore.config;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VNPAYConfig {
    public static final String vnp_PayUrl     = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String vnp_Returnurl  = "/vnpay-payment-return";
    public static final String vnp_TmnCode    = "W40CBXJ2";
    public static final String vnp_HashSecret = "4HV5CYVPSPY36SBPSV9IEKDSPR276SRF";

    /**
     * Sinh secure hash (vnp_SecureHash) trên tất cả các params (đã có cả vnp_SecureHashType)
     */
    public static String hashAllFields(Map<String, String> fields) {
        // 1. Sắp xếp key tăng dần
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        // 2. Build chuỗi raw "key=value&key2=value2..."
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> it = fieldNames.iterator(); it.hasNext(); ) {
            String key = it.next();
            String value = fields.get(key);
            if (value != null && !value.isEmpty()) {
                sb.append(key).append("=").append(value);
                if (it.hasNext()) {
                    sb.append("&");
                }
            }
        }

        // 3. Trả về HMAC SHA512
        return hmacSHA512(vnp_HashSecret, sb.toString());
    }

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException("Key or data is null");
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate HMAC SHA512", ex);
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        return (ip == null || ip.isEmpty())
                ? request.getRemoteAddr()
                : ip;
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(rnd.nextInt(10));
        }
        return sb.toString();
    }
}
