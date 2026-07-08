package frolenko.supermarketweb.dto.product;

import lombok.Builder;

@Builder
public record ProductListDTO(
        int id,
        String name,
        String categoryName
) {
}