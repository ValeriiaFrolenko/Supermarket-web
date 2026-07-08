package frolenko.supermarketweb.dto.analytics;

import lombok.Builder;

@Builder
public record EmployeePerformanceDTO(
        String employeeId,
        String cashierName,
        int receiptCount,
        double totalAmount
) {}