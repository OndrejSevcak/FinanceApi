package com.example.demo.services;

import com.example.demo.api.dto.FrankfurterResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class FxService {

    private static final String apiUrl = "https://api.frankfurter.dev/v1/latest";
    private final RestTemplate restTemplate = new RestTemplate();


    // In real implementation this would call an external/internal FX service.
    public BigDecimal getRate(String fromCurrency, String toCurrency) {
        if (fromCurrency == null || toCurrency == null) return BigDecimal.ONE;
        if (fromCurrency.trim().equalsIgnoreCase(toCurrency.trim())) return BigDecimal.ONE;

        String to = toCurrency.trim().toUpperCase();
        try
        {
            String url = apiUrl
                    + "?base=" + URLEncoder.encode(fromCurrency.trim().toUpperCase(), StandardCharsets.UTF_8)
                    + "&symbols=" + URLEncoder.encode(toCurrency.trim().toUpperCase(), StandardCharsets.UTF_8);

            FrankfurterResponse resp = restTemplate.getForObject(url, FrankfurterResponse.class);
            if (resp != null && resp.getRates() != null && resp.getRates().containsKey(to)) {
                BigDecimal rate = resp.getRates().get(to);
                return rate.setScale(18, RoundingMode.HALF_UP);
            }
        }
        catch(Exception ex){
            System.out.println("Exception in FX Service: " + ex.getMessage());
        }

        // simple mock: EUR->BTC ~ 0.00002, BTC->EUR ~ 50000 (inverse)
        if (fromCurrency.equalsIgnoreCase("EUR") && toCurrency.equalsIgnoreCase("BTC")) {
            return new BigDecimal("0.00002");
        }
        if (fromCurrency.equalsIgnoreCase("BTC") && toCurrency.equalsIgnoreCase("EUR")) {
            return new BigDecimal("50000");
        }
        // default fallback
        return BigDecimal.ONE.setScale(18, RoundingMode.HALF_UP);
    }
}

