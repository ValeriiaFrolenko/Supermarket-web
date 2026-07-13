package frolenko.supermarketweb.dto.employee;

import frolenko.supermarketweb.enums.employee.EmployeeRole;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record EmployeeDetailsDTO(
        String idEmployee,
        String emplSurname,
        String emplName,
        String emplPatronymic,
        EmployeeRole emplRole,
        double salary,
        LocalDate dateOfBirth,
        LocalDate dateOfStart,
        String phoneNumber,
        String city,
        String street,
        String zipCode
) {
}