package frolenko.supermarketweb.enums.sortby;

public enum ProductSortBy implements SortBy{
    NAME("product_name"),
    CATEGORY("category_name");

    private final String column;

    ProductSortBy(String column) {
        this.column = column;
    }

    @Override
    public String getColumn() {
        return column;
    }
}