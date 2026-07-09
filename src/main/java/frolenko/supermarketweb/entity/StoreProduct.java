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
@Table(name = "store_product", indexes = {
        @Index(name = "idx_store_product_product", columnList = "id_product"),
        @Index(name = "idx_store_product_promotional", columnList = "promotional_product")
})
public class StoreProduct {
    @Id
    @Column(name = "upc", nullable = false, length = 12)
    private String upc;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "upc_prom")
    private StoreProduct upcProm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_product", nullable = false)
    private Product idProduct;

    @Column(name = "selling_price", nullable = false, precision = 13, scale = 4)
    private BigDecimal sellingPrice;

    @Column(name = "products_number", nullable = false)
    private Integer productsNumber;

    @Column(name = "promotional_product", nullable = false)
    private Boolean promotionalProduct = false;

}