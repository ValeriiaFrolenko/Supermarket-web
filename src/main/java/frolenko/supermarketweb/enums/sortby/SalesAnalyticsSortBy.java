package frolenko.supermarketweb.enums.sortby;

public enum SalesAnalyticsSortBy implements SortBy {
    TOTAL_AMOUNT ("total_amount DESC"),
    QUANTITY_SOLD("quantity_sold DESC"),
    DATE         ("c.print_date DESC"),
    PRODUCT_NAME ("p.product_name ASC"),
    CHECK_NUMBER ("c.check_number ASC");

    private final String column;

    SalesAnalyticsSortBy(String column) {
        this.column = column;
    }

    @Override
    public String getColumn() {
        return column;
    }
}