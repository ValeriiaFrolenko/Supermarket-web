package frolenko.supermarketweb.filter;

import frolenko.supermarketweb.enums.sortby.CheckSortBy;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class CheckFilter {
    private String checkNumber;
    private String cashierSurname;
    private String employeeId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private CheckSortBy sortBy;
    private boolean asc = true;

    public boolean isEmpty() {
        return checkNumber == null &&
                cashierSurname == null &&
                employeeId == null &&
                dateFrom == null &&
                dateTo == null &&
                sortBy == null;
    }
}