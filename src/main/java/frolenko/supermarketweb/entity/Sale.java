package frolenko.supermarketweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @MapsId("upc")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "upc", nullable = false)
    private StoreProduct upc;

    @MapsId("checkNumber")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "check_number", nullable = false)
    private CheckTable checkNumber;

    @Column(name = "product_number", nullable = false)
    private Integer productNumber;

    @Column(name = "selling_price", nullable = false, precision = 13, scale = 4)
    private BigDecimal sellingPrice;

}