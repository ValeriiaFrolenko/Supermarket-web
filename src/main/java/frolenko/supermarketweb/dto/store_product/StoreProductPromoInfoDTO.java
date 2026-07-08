package frolenko.supermarketweb.dto.store_product;

import lombok.Builder;

@Builder
public record StoreProductPromoInfoDTO(
        Integer productId,
        boolean usedAsPromoBase
) {
}