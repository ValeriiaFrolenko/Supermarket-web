package frolenko.supermarketweb.enums.sortby;

public enum CustomerCardSortBy implements SortBy{
    SURNAME("cust_surname"),
    DISCOUNT("percent");

    private final String column;

    CustomerCardSortBy(String column) {
        this.column = column;
    }

    @Override
    public String getColumn() {
        return column;
    }
}