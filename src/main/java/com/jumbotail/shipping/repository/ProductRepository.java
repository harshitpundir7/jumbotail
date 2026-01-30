package com.jumbotail.shipping.repository;

import com.jumbotail.shipping.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Product entity CRUD operations.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find product by SKU/product code.
     */
    Optional<Product> findByProductId(String productId);

    /**
     * Find all active products by seller.
     */
    List<Product> findBySellerIdAndIsActiveTrue(Long sellerId);

    /**
     * Find all products by category.
     */
    List<Product> findByCategoryAndIsActiveTrue(String category);

    /**
     * Search products by name (case-insensitive).
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.isActive = true")
    List<Product> searchByName(@Param("searchTerm") String searchTerm);
}
