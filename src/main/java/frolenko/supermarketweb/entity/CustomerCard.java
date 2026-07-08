package frolenko.supermarketweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customer_card", indexes = {
        @Index(name = "idx_customer_surname", columnList = "cust_surname")
})
public class CustomerCard {
    @Id
    @Column(name = "card_number", nullable = false, length = 13)
    private String cardNumber;

    @Column(name = "cust_surname", nullable = false, length = 50)
    private String custSurname;

    @Column(name = "cust_name", nullable = false, length = 50)
    private String custName;

    @Column(name = "cust_patronymic", length = 50)
    private String custPatronymic;

    @Column(name = "phone_number", nullable = false, length = 13)
    private String phoneNumber;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "street", length = 50)
    private String street;

    @Column(name = "zip_code", length = 9)
    private String zipCode;

    @Column(name = "percent", nullable = false)
    private Integer percent;

}