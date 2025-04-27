package hcmute.edu.vn.techstore.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Component
public class PriceUtil {
    public String formatPrice(BigDecimal price) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        currencyFormat.setMinimumFractionDigits(0);
        currencyFormat.setMaximumFractionDigits(0);
        return currencyFormat.format(price);
    }

    public BigDecimal parsePrice(String formattedPrice) {
        String cleaned = formattedPrice
                .replaceAll("[^\\d,]", "") // chỉ giữ số và dấu phẩy
                .replace(",", ".");        // đổi dấu phẩy thành dấu chấm nếu có
        return new BigDecimal(cleaned);
    }
}
