package com.jumbotail.shipping.repository;

import com.jumbotail.shipping.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Seller entity CRUD operations.
 */
@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    /**
     * Find seller by business identifier.
     */
    Optional<Seller> findBySellerId(String sellerId);

    /**
     * Check if seller exists by business identifier.
     */
    boolean existsBySellerId(String sellerId);
}
