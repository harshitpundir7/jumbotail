package com.jumbotail.shipping.service;

import com.jumbotail.shipping.entity.Seller;
import com.jumbotail.shipping.entity.Warehouse;
import com.jumbotail.shipping.entity.embeddable.GeoLocation;
import com.jumbotail.shipping.exception.ResourceNotFoundException;
import com.jumbotail.shipping.repository.SellerRepository;
import com.jumbotail.shipping.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Service for warehouse-related operations.
 * Handles finding the nearest warehouse to a seller's location.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final SellerRepository sellerRepository;
    private final GeoLocationService geoLocationService;

    /**
     * Finds the nearest active warehouse to a seller's location.
     * Results are cached to improve performance for repeated queries.
     * 
     * @param sellerId the seller's database ID
     * @return the nearest active warehouse
     * @throws ResourceNotFoundException if seller not found or no active warehouses
     *                                   available
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "nearestWarehouse", key = "#sellerId")
    public Warehouse findNearestWarehouse(Long sellerId) {
        log.info("Finding nearest warehouse for seller ID: {}", sellerId);

        // Fetch seller with location
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", sellerId));

        if (seller.getLocation() == null) {
            throw new ResourceNotFoundException(
                    String.format("Seller with id '%d' does not have location information", sellerId));
        }

        return findNearestWarehouseToLocation(seller.getLocation());
    }

    /**
     * Finds the nearest active warehouse to a given location.
     * 
     * @param location the reference location
     * @return the nearest active warehouse
     * @throws ResourceNotFoundException if no active warehouses available
     */
    @Transactional(readOnly = true)
    public Warehouse findNearestWarehouseToLocation(GeoLocation location) {
        log.debug("Finding nearest warehouse to location: {}", location.toCoordinateString());

        List<Warehouse> activeWarehouses = warehouseRepository.findByIsActiveTrue();

        if (activeWarehouses.isEmpty()) {
            log.error("No active warehouses found in the system");
            throw new ResourceNotFoundException("No active warehouses available in the system");
        }

        // Find warehouse with minimum distance
        Warehouse nearestWarehouse = activeWarehouses.stream()
                .filter(w -> w.getLocation() != null)
                .min(Comparator.comparingDouble(
                        warehouse -> geoLocationService.calculateDistanceInKm(location, warehouse.getLocation())))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No warehouses with valid location found"));

        double distance = geoLocationService.calculateDistanceInKm(location, nearestWarehouse.getLocation());
        log.info("Nearest warehouse: {} (ID: {}) at distance: {:.2f} km",
                nearestWarehouse.getName(), nearestWarehouse.getId(), distance);

        return nearestWarehouse;
    }

    /**
     * Gets a warehouse by its ID.
     * 
     * @param warehouseId the warehouse database ID
     * @return the warehouse entity
     * @throws ResourceNotFoundException if warehouse not found
     */
    @Transactional(readOnly = true)
    public Warehouse getWarehouseById(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", warehouseId));
    }

    /**
     * Gets all active warehouses.
     * 
     * @return list of active warehouses
     */
    @Transactional(readOnly = true)
    public List<Warehouse> getAllActiveWarehouses() {
        return warehouseRepository.findByIsActiveTrue();
    }

    /**
     * Calculates the distance between a warehouse and a customer location.
     * 
     * @param warehouseId      warehouse database ID
     * @param customerLocation customer's geographical location
     * @return distance in kilometers
     */
    @Transactional(readOnly = true)
    public double calculateDistanceToCustomer(Long warehouseId, GeoLocation customerLocation) {
        Warehouse warehouse = getWarehouseById(warehouseId);

        if (warehouse.getLocation() == null) {
            throw new ResourceNotFoundException(
                    String.format("Warehouse with id '%d' does not have location information", warehouseId));
        }

        return geoLocationService.calculateDistanceInKm(warehouse.getLocation(), customerLocation);
    }
}
