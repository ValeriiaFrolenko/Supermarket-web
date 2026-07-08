package frolenko.supermarketweb.dto.sale;

import lombok.Builder;

@Builder
public record SaleListDTO(
        String UPC,
        String productName,
        int quantity,
        double unitPrice,
        double totalPrice
) {
}