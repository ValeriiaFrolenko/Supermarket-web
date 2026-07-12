package frolenko.supermarketweb.filter;

import frolenko.supermarketweb.enums.sortby.SalesAnalyticsSortBy;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class SalesAnalyticsFilter {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Integer productId;
    private String employeeId;
    private SalesAnalyticsSortBy sortBy;

    public boolean isEmpty() {
        return dateFrom == null &&
                dateTo == null &&
                productId == null &&
                employeeId == null &&
                sortBy == null;
    }
}