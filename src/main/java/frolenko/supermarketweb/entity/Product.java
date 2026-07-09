package frolenko.supermarketweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "product", indexes = {
        @Index(name = "idx_product_category", columnList = "category_number"),
        @Index(name = "idx_product_name", columnList = "product_name")
})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "category_number", nullable = false)
    private Category categoryNumber;

    @Column(name = "product_name", nullable = false, length = 50)
    private String productName;

    @Column(name = "manufacturer", nullable = false, length = 50)
    private String manufacturer;

    @Column(name = "characteristics", nullable = false, length = 100)
    private String characteristics;

}