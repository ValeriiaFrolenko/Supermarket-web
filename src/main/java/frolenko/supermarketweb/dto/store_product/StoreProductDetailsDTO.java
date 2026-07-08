package frolenko.supermarketweb.dto.store_product;

import lombok.Builder;

@Builder
public record StoreProductDetailsDTO(
        String UPC,
        String UPCprom,
        int productId,
        String productName,
        String categoryName,
        double price,
        int quantity,
        Boolean promotional,
        Double discount
) {
}
