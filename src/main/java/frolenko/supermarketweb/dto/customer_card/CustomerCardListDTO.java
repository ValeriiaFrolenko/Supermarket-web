package frolenko.supermarketweb.dto.customer_card;

import lombok.Builder;

@Builder
public record CustomerCardListDTO(
        String cardNumber,
        String surname,
        String name,
        int discount,
        String phone
) {
}