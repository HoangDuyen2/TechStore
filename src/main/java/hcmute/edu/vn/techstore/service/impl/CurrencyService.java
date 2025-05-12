package hcmute.edu.vn.techstore.service.impl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Profile("dev")
public class CurrencyService {

    @Value("${currency.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal getExchangeRateVNDToUSD() {
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
        if (response == null || !response.containsKey("conversion_rates")) {
            throw new RuntimeException("Invalid response from currency API");
        }

        Map<String, Object> rates = (Map<String, Object>) response.get("conversion_rates");
        Double rate = (Double) rates.get("USD");

        if (rate == null || rate <= 0) {
            throw new RuntimeException("Invalid USD exchange rate");
        }

        return BigDecimal.valueOf(rate);
    }
}