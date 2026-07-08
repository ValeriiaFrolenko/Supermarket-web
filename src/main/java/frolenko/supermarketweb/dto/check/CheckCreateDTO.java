package frolenko.supermarketweb.dto.check;

import frolenko.supermarketweb.dto.sale.SaleCreateDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record CheckCreateDTO(
        String checkNumber,
        String idEmployee,
        String cardNumber,
        Boolean useDiscount,
        List<SaleCreateDTO> sales
) {}
