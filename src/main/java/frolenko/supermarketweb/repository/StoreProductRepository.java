package frolenko.supermarketweb.repository;

import frolenko.supermarketweb.entity.StoreProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoreProductRepository extends JpaRepository<StoreProduct, String> {
    @Query("SELECT sp FROM StoreProduct sp JOIN FETCH sp.idProduct p JOIN FETCH p.categoryNumber")
    List<StoreProduct> findAllDetails();

    @Query(value = """
    SELECT upc FROM store_product WHERE promotional_product = true
    UNION
    SELECT upc_prom FROM store_product WHERE promotional_product = true
    """, nativeQuery = true)
    List<String> findBlockedUPCs();

    List<StoreProduct> findByProductsNumber(Integer productsNumber);

    boolean existsByIdProduct_IdAndPromotionalProductFalse(Integer productId);
}


