package frolenko.supermarketweb.repository;

import frolenko.supermarketweb.entity.CustomerCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerCardRepository extends JpaRepository<CustomerCard, String> {
}
