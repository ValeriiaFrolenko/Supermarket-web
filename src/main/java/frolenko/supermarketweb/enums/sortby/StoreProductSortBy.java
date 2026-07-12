package frolenko.supermarketweb.enums.sortby;

public enum StoreProductSortBy implements SortBy{
    NAME("product_name"),
    QUANTITY("products_number"),
    PRICE("selling_price");

    private final String column;

    StoreProductSortBy(String column) {
        this.column = column;
    }

    @Override
    public String getColumn() {
        return column;
    }
}