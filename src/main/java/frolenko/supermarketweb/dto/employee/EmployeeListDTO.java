package frolenko.supermarketweb.dto.employee;

import lombok.Builder;

@Builder
public record EmployeeListDTO(
        String idEmployee,
        String emplSurname,
        String emplName,
        String emplRole,
        String phoneNumber
) {
}