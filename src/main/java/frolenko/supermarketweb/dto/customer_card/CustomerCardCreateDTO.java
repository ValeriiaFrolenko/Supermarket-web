package frolenko.supermarketweb.dto.customer_card;

import lombok.Builder;

@Builder
public record CustomerCardCreateDTO(
        String cardNumber,
        String surname,
        String name,
        String patronymic,
        String phoneNumber,
        String city,
        String street,
        String zipCode,
        int discount
) {
}