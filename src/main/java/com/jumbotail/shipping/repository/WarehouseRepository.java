package com.jumbotail.shipping.repository;

import com.jumbotail.shipping.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Warehouse entity CRUD operations.
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    /**
     * Find warehouse by code.
     */
    Optional<Warehouse> findByWarehouseCode(String warehouseCode);

    /**
     * Find all active warehouses.
     */
    List<Warehouse> findByIsActiveTrue();

    /**
     * Find warehouses by city.
     */
    List<Warehouse> findByCityAndIsActiveTrue(String city);

    /**
     * Check if warehouse exists by code.
     */
    boolean existsByWarehouseCode(String warehouseCode);
}
