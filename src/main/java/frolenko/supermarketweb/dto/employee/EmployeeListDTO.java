package frolenko.supermarketweb.dto.employee;

import frolenko.supermarketweb.enums.employee.EmployeeRole;
import lombok.Builder;

@Builder
public record EmployeeListDTO(
        String id,
        String surname,
        String name,
        EmployeeRole role,
        String phoneNumber
        ) {
}