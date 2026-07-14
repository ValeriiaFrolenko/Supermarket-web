package frolenko.supermarketweb.filter;

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

    public boolean isEmpty() {
        return dateFrom == null &&
                dateTo == null &&
                productId == null &&
                employeeId == null;
    }
}