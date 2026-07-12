package frolenko.supermarketweb.filter;

import frolenko.supermarketweb.enums.sortby.ProductSortBy;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductFilter {
    private String name;
    private Integer categoryId;
    private ProductSortBy sortBy;

    public boolean isEmpty() {
        return getName() == null &&
                categoryId == null &&
                sortBy == null;
    }
}