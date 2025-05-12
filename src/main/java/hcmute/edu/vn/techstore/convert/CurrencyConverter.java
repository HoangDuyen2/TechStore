package hcmute.edu.vn.techstore.convert;

import java.math.BigDecimal;

public interface CurrencyConverter {
    BigDecimal convert(BigDecimal amount);
} 