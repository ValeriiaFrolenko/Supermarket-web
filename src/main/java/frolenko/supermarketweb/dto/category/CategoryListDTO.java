package frolenko.supermarketweb.dto.category;

public record CategoryListDTO(
        int id,
        String name
) {
    @Override
    public String toString() {
        return name;
    }
}