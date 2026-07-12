package frolenko.supermarketweb.enums.sortby;

public enum CheckSortBy implements SortBy {
    DATE("print_date"),
    EMPLOYEE("empl_surname"),
    SUM_TOTAL("sum_total");

    private final String column;

    CheckSortBy(String column) {
        this.column = column;
    }

    @Override
    public String getColumn() {
        return column;
    }
}