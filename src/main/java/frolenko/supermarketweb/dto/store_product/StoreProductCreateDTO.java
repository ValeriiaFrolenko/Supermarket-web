package frolenko.supermarketweb.dto.store_product;

import lombok.Builder;

@Builder
public record StoreProductCreateDTO(
        String UPC,
        String UPCprom,
        int productId,
        double price,
        int quantity,
        Boolean promotional,
        Double discount
) {
}
