package frolenko.supermarketweb.repository;

import frolenko.supermarketweb.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p JOIN FETCH p.categoryNumber")
    List<Product> findAllDetails();
}
