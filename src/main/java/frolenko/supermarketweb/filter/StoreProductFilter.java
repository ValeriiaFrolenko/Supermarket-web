package frolenko.supermarketweb.filter;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StoreProductFilter {
    private String upc;
    private String productName;
    private Integer categoryId;
    private Boolean promotional;

    public boolean isEmpty() {
        return upc == null &&
                productName == null &&
                categoryId == null &&
                promotional == null;
    }
}