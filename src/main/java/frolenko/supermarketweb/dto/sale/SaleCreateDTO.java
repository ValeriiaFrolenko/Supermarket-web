package frolenko.supermarketweb.dto.sale;

public record SaleCreateDTO(
        String UPC,
        String checkNumber,
        int quantity
){
}