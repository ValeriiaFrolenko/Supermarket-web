package frolenko.supermarketweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "check_table", indexes = {
        @Index(name = "idx_check_employee", columnList = "id_employee"),
        @Index(name = "idx_check_card", columnList = "card_number"),
        @Index(name = "idx_check_date", columnList = "print_date")
})
public class CheckTable {
    @Id
    @Column(name = "check_number", nullable = false, length = 10)
    private String checkNumber;

    @Column(name = "id_employee", nullable = false)
    private String idEmployee;

    @Column(name = "card_number")
    private String cardNumber;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "print_date", nullable = false)
    private LocalDateTime printDate;

    @Column(name = "sum_total", nullable = false, precision = 13, scale = 4)
    private BigDecimal sumTotal;

    @Column(name = "vat", nullable = false, precision = 13, scale = 4)
    private BigDecimal vat;

}