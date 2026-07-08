package frolenko.supermarketweb.dto.product;

import lombok.Builder;

@Builder
public record ProductDetailsDTO(
        int id,
        String name,
        int categoryId,
        String categoryName,
        String manufacturer,
        String characteristics
) {
}