package frolenko.supermarketweb.filter;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class EmployeePerformanceFilter {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private boolean onlyWithCardAlways;

    public boolean isEmpty() {
        return dateFrom == null &&
                dateTo == null &&
                !onlyWithCardAlways;
    }
}