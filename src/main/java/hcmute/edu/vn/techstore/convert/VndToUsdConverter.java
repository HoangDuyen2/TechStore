package hcmute.edu.vn.techstore.convert;

import hcmute.edu.vn.techstore.service.impl.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class VndToUsdConverter implements CurrencyConverter {

    private final CurrencyService currencyService;

    public VndToUsdConverter(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Override
    public BigDecimal convert(BigDecimal vndAmount) {
        try {
            // Gọi API để lấy tỉ giá động VND -> USD
            BigDecimal exchangeRate = currencyService.getExchangeRateVNDToUSD();

            // Thực hiện chuyển đổi
            BigDecimal usdAmount = vndAmount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
            log.info("Converted {} VND to {} USD using exchange rate {}", vndAmount, usdAmount, exchangeRate);
            return usdAmount;
        } catch (Exception e) {
            log.error("Error converting currency: {}", e.getMessage(), e);
            throw new RuntimeException("Could not convert VND to USD", e);
        }
    }
}