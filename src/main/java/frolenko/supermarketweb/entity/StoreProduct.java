package frolenko.supermarketweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "upc_prom")
    private String upcProm;

    @Column(name = "id_product", nullable = false)
    private Integer idProduct;

    @Column(name = "selling_price", nullable = false, precision = 13, scale = 4)
    private BigDecimal sellingPrice;

    @Column(name = "products_number", nullable = false)
    private Integer productsNumber;

    @Column(name = "promotional_product", nullable = false)
    private Boolean promotionalProduct = false;

}