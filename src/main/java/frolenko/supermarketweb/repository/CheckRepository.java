package frolenko.supermarketweb.repository;

import frolenko.supermarketweb.entity.CheckTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CheckRepository extends JpaRepository<CheckTable, String> {

    @Query("SELECT c FROM CheckTable c JOIN FETCH c.idEmployee LEFT JOIN FETCH c.cardNumber")
    List<CheckTable> findAllDetails();
}
