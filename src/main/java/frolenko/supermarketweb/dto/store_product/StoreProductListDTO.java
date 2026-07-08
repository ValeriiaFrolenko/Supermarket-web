package frolenko.supermarketweb.dto.store_product;
import lombok.Builder;

@Builder
public record StoreProductListDTO(
        String UPC,
        String productName,
        double price,
        int quantity,
        Boolean promotional
) {}