package frolenko.supermarketweb.dto.analytics;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SalesAnalyticsDTO(
        String        productName,
        int           quantitySold,
        double        totalAmount,
        String        checkNumber,
        String        cashierName,
        LocalDateTime printDate
) {}