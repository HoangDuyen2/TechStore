package hcmute.edu.vn.techstore.convert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class VndToUsdConverter implements CurrencyConverter {
    private static final BigDecimal EXCHANGE_RATE = new BigDecimal("0.000041"); // 1 VND = 0.000041 USD

    @Override
    public BigDecimal convert(BigDecimal vndAmount) {
        BigDecimal usdAmount = vndAmount.multiply(EXCHANGE_RATE).setScale(2, RoundingMode.HALF_UP);
        return usdAmount;
    }
} 