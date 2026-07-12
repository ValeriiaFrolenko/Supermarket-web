package frolenko.supermarketweb.dto.customer_card;

import lombok.Builder;

@Builder
public record CustomerCardListDTO(
        String cardNumber,
        String custSurname,
        String custName,
        int percent,
        String phoneNumber
) {
}