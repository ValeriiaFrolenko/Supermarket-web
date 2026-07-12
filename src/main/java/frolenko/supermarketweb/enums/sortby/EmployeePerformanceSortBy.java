package frolenko.supermarketweb.enums.sortby;

public enum EmployeePerformanceSortBy implements SortBy {
    TOTAL_AMOUNT ("total_amount DESC"),
    RECEIPT_COUNT("receipt_count DESC"),
    CASHIER_NAME ("cashier_name ASC");

    private final String column;

    EmployeePerformanceSortBy(String column) {
        this.column = column;
    }

    @Override
    public String getColumn() {
        return column;
    }
}