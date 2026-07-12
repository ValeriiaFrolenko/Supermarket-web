package frolenko.supermarketweb.filter;

import frolenko.supermarketweb.enums.sortby.StoreProductSortBy;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StoreProductFilter {
    private String upc;
    private String productName;
    private Integer categoryId;
    private Boolean promotional;
    private StoreProductSortBy sortBy;

    public boolean isEmpty() {
        return upc == null &&
                productName == null &&
                categoryId == null &&
                promotional == null &&
                sortBy == null;
    }
}