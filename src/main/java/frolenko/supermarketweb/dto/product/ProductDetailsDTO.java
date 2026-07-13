package frolenko.supermarketweb.dto.product;

import lombok.Builder;

@Builder
public record ProductDetailsDTO(
        int idProduct,
        String productName,
        int categoryNumber,
        String categoryName,
        String manufacturer,
        String characteristics
) {
}