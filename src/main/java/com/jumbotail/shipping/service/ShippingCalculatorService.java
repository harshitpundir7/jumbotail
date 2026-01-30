package com.jumbotail.shipping.service;

import com.jumbotail.shipping.entity.Customer;
import com.jumbotail.shipping.entity.Product;
import com.jumbotail.shipping.entity.Seller;
import com.jumbotail.shipping.entity.Warehouse;
import com.jumbotail.shipping.entity.embeddable.GeoLocation;
import com.jumbotail.shipping.enums.DeliverySpeed;
import com.jumbotail.shipping.enums.TransportMode;
import com.jumbotail.shipping.exception.InvalidRequestException;
import com.jumbotail.shipping.exception.ResourceNotFoundException;
import com.jumbotail.shipping.repository.CustomerRepository;
import com.jumbotail.shipping.repository.ProductRepository;
import com.jumbotail.shipping.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service for calculating shipping charges.
 * Implements the core business logic for shipping cost estimation.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShippingCalculatorService {

    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final WarehouseService warehouseService;
    private final GeoLocationService geoLocationService;

    /**
     * Default weight in kg when product is not specified.
     */
    private static final double DEFAULT_WEIGHT_KG = 1.0;

    /**
     * Calculates shipping charge from a warehouse to a customer.
     * 
     * @param warehouseId   warehouse database ID
     * @param customerId    customer database ID
     * @param deliverySpeed delivery speed option
     * @param productId     optional product ID for weight-based calculation
     * @return calculated shipping charge in INR
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "shippingCharges", key = "#warehouseId + '_' + #customerId + '_' + #deliverySpeed + '_' + #productId")
    public BigDecimal calculateShippingCharge(Long warehouseId, Long customerId,
            DeliverySpeed deliverySpeed, Long productId) {
        log.info("Calculating shipping charge: warehouse={}, customer={}, speed={}, product={}",
                warehouseId, customerId, deliverySpeed, productId);

        // Validate and fetch entities
        Warehouse warehouse = warehouseService.getWarehouseById(warehouseId);
        Customer customer = getCustomerById(customerId);

        // Validate locations
        validateLocations(warehouse, customer);

        // Get weight for calculation
        double weightKg = getWeightForCalculation(productId);

        // Calculate distance
        double distanceKm = geoLocationService.calculateDistanceInKm(
                warehouse.getLocation(), customer.getLocation());

        // Calculate charge
        return calculateTotalCharge(distanceKm, weightKg, deliverySpeed);
    }

    /**
     * Calculates shipping charge from seller to customer via nearest warehouse.
     * This is the combined calculation that first finds the nearest warehouse,
     * then calculates the shipping cost.
     * 
     * @param sellerId      seller database ID
     * @param customerId    customer database ID
     * @param deliverySpeed delivery speed option
     * @param productId     optional product ID for weight-based calculation
     * @return calculated shipping charge in INR
     */
    @Transactional(readOnly = true)
    public ShippingCalculationResult calculateShippingForSellerAndCustomer(
            Long sellerId, Long customerId, DeliverySpeed deliverySpeed, Long productId) {

        log.info("Calculating complete shipping: seller={}, customer={}, speed={}, product={}",
                sellerId, customerId, deliverySpeed, productId);

        // Find nearest warehouse to seller
        Warehouse nearestWarehouse = warehouseService.findNearestWarehouse(sellerId);

        // Calculate shipping charge from warehouse to customer
        BigDecimal shippingCharge = calculateShippingCharge(
                nearestWarehouse.getId(), customerId, deliverySpeed, productId);

        return new ShippingCalculationResult(shippingCharge, nearestWarehouse);
    }

    /**
     * Calculates the total shipping charge including transport and delivery speed
     * charges.
     * 
     * @param distanceKm    distance in kilometers
     * @param weightKg      weight in kilograms
     * @param deliverySpeed delivery speed option
     * @return total charge rounded to 2 decimal places
     */
    public BigDecimal calculateTotalCharge(double distanceKm, double weightKg, DeliverySpeed deliverySpeed) {
        // Determine transport mode based on distance
        TransportMode transportMode = TransportMode.getByDistance(distanceKm);

        log.debug("Selected transport mode: {} for distance: {} km", transportMode, distanceKm);

        // Calculate transport charge
        double transportCharge = transportMode.calculateCharge(distanceKm, weightKg);

        // Calculate delivery speed charge
        double deliveryCharge = deliverySpeed.calculateDeliveryCharge(weightKg);

        // Total charge
        double totalCharge = transportCharge + deliveryCharge;

        log.debug("Charge breakdown: transport={}, delivery={}, total={}",
                String.format("%.2f", transportCharge),
                String.format("%.2f", deliveryCharge),
                String.format("%.2f", totalCharge));

        // Round to 2 decimal places
        return BigDecimal.valueOf(totalCharge).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Gets a customer by ID.
     */
    private Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
    }

    /**
     * Gets a seller by ID.
     */
    private Seller getSellerById(Long sellerId) {
        return sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", sellerId));
    }

    /**
     * Gets the weight for shipping calculation.
     * If product is specified, uses the product's chargeable weight.
     * Otherwise, uses default weight.
     */
    private double getWeightForCalculation(Long productId) {
        if (productId == null) {
            log.debug("No product specified, using default weight: {} kg", DEFAULT_WEIGHT_KG);
            return DEFAULT_WEIGHT_KG;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        double weight = product.getChargeableWeight();
        log.debug("Using product chargeable weight: {} kg", weight);
        return weight;
    }

    /**
     * Validates that both warehouse and customer have valid locations.
     */
    private void validateLocations(Warehouse warehouse, Customer customer) {
        if (warehouse.getLocation() == null) {
            throw new InvalidRequestException(
                    String.format("Warehouse '%s' does not have location information",
                            warehouse.getWarehouseCode()));
        }

        if (customer.getLocation() == null) {
            throw new InvalidRequestException(
                    String.format("Customer '%s' does not have location information",
                            customer.getCustomerId()));
        }
    }

    /**
     * Result object for shipping calculation containing charge and warehouse info.
     */
    public record ShippingCalculationResult(BigDecimal shippingCharge, Warehouse warehouse) {
    }
}
