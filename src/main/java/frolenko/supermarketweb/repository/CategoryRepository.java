package frolenko.supermarketweb.repository;

import frolenko.supermarketweb.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByCategoryName(String name);
}
