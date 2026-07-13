package frolenko.supermarketweb.dto.product;

import lombok.Builder;

@Builder
public record ProductListDTO(
        int idProduct,
        String productName,
        String categoryName
) {
}