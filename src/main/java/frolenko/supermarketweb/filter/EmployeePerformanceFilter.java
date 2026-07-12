package frolenko.supermarketweb.filter;

import frolenko.supermarketweb.enums.sortby.EmployeePerformanceSortBy;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class EmployeePerformanceFilter {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private boolean onlyWithCardAlways;
    private EmployeePerformanceSortBy sortBy;

    public boolean isEmpty() {
        return dateFrom == null &&
                dateTo == null &&
                !onlyWithCardAlways &&
                sortBy == null;
    }
}