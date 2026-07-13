package frolenko.supermarketweb.filter;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductFilter {
    private String name;
    private Integer categoryId;

    public boolean isEmpty() {
        return name == null && categoryId == null;
    }
}