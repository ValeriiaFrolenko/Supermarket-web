package frolenko.supermarketweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "sale", indexes = {
        @Index(name = "idx_sale_check", columnList = "check_number")
})
public class Sale {
    @EmbeddedId
    private SaleId id;

    @Column(name = "product_number", nullable = false)
    private Integer productNumber;

    @Column(name = "selling_price", nullable = false, precision = 13, scale = 4)
    private BigDecimal sellingPrice;

}