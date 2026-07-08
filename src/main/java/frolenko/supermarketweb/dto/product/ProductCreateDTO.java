package frolenko.supermarketweb.dto.product;

public record ProductCreateDTO(
        int id,
        int categoryId,
        String name,
        String manufacturer,
        String characteristics
) {
}