package frolenko.supermarketweb.dto.store_product;
import lombok.Builder;

@Builder
public record StoreProductListDTO(
        String upc,
        String productName,
        double sellingPrice,
        int productNumber,
        Boolean promotionalProduct
) {}