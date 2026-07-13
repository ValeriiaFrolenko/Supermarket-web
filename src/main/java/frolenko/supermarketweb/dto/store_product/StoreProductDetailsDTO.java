package frolenko.supermarketweb.dto.store_product;

import lombok.Builder;

@Builder
public record StoreProductDetailsDTO(
        String upc,
        String upcProm,
        int idProduct,
        String productName,
        String categoryName,
        double sellingPrice,
        int productNumber,
        Boolean promotionalProduct,
        Double discount
) {
}
