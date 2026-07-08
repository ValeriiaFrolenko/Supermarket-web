package frolenko.supermarketweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "employee", indexes = {
        @Index(name = "idx_employee_surname", columnList = "empl_surname"),
        @Index(name = "idx_employee_role", columnList = "empl_role")
})
public class Employee {
    @Id
    @Column(name = "id_employee", nullable = false, length = 10)
    private String idEmployee;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "empl_surname", nullable = false, length = 50)
    private String emplSurname;

    @Column(name = "empl_name", nullable = false, length = 50)
    private String emplName;

    @Column(name = "empl_patronymic", length = 50)
    private String emplPatronymic;

    @Column(name = "empl_role", nullable = false, length = 10)
    private String emplRole;

    @Column(name = "salary", nullable = false, precision = 13, scale = 4)
    private BigDecimal salary;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "date_of_start", nullable = false)
    private LocalDate dateOfStart;

    @Column(name = "phone_number", nullable = false, length = 13)
    private String phoneNumber;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "street", nullable = false, length = 50)
    private String street;

    @Column(name = "zip_code", nullable = false, length = 9)
    private String zipCode;

}