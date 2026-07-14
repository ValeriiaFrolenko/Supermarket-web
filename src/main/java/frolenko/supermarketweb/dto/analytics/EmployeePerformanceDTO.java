package frolenko.supermarketweb.dto.analytics;

import lombok.Builder;

@Builder
public record EmployeePerformanceDTO(
        String idEmployee,
        String cashierName,
        int receiptCount,
        double totalAmount
) {}