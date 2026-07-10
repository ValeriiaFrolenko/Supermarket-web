package frolenko.supermarketweb.repository;

import frolenko.supermarketweb.entity.CheckTable;
import frolenko.supermarketweb.entity.Sale;
import frolenko.supermarketweb.entity.SaleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, SaleId> {
    @Query("""
        SELECT s FROM Sale s
        JOIN FETCH s.upc sp
        JOIN FETCH sp.idProduct p
        WHERE s.checkNumber.checkNumber = :checkNumber
        ORDER BY p.productName
        """)
    List<Sale> findByCheckNumber(@Param("checkNumber") String checkNumber);
}
