package frolenko.supermarketweb.dto.check;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CheckDetailsDTO(
        String checkNumber,
        String employeeName,
        String cardNumber,
        String customerName,
        LocalDateTime printDate,
        double sumTotal,
        double vat,
        double baseSum,
        double discountAmount
) {}