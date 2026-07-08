package frolenko.supermarketweb.dto.product;

public record ProductNameDTO(
        int id,
        String name
) {
    @Override
    public String toString() { return name; }
}