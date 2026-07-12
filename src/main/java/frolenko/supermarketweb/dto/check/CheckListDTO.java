package frolenko.supermarketweb.dto.check;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CheckListDTO(
        String checkNumber,
        String employeeName,
        LocalDateTime printDate,
        double sumTotal
) {
}