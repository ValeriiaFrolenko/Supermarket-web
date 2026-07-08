package frolenko.supermarketweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_number", nullable = false)
    private Integer id;

    @Column(name = "category_name", nullable = false, length = 50)
    private String categoryName;

}