package frolenko.supermarketweb.filter;

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

    public boolean isEmpty() {
        return checkNumber == null &&
                cashierSurname == null &&
                employeeId == null &&
                dateFrom == null &&
                dateTo == null;
    }
}