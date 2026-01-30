package com.jumbotail.shipping.controller;

import com.jumbotail.shipping.dto.request.ShippingCalculateRequest;
import com.jumbotail.shipping.dto.response.LocationResponse;
import com.jumbotail.shipping.dto.response.NearestWarehouseResponse;
import com.jumbotail.shipping.dto.response.ShippingCalculateResponse;
import com.jumbotail.shipping.dto.response.ShippingChargeResponse;
import com.jumbotail.shipping.entity.Customer;
import com.jumbotail.shipping.entity.Product;
import com.jumbotail.shipping.entity.Seller;
import com.jumbotail.shipping.entity.Warehouse;
import com.jumbotail.shipping.enums.DeliverySpeed;
import com.jumbotail.shipping.enums.TransportMode;
import com.jumbotail.shipping.exception.ResourceNotFoundException;
import com.jumbotail.shipping.repository.CustomerRepository;
import com.jumbotail.shipping.repository.ProductRepository;
import com.jumbotail.shipping.repository.SellerRepository;
import com.jumbotail.shipping.service.GeoLocationService;
import com.jumbotail.shipping.service.ShippingCalculatorService;
import com.jumbotail.shipping.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST Controller for shipping charge calculation.
 * Provides APIs to calculate shipping costs based on distance, weight, and
 * delivery speed.
 */
@RestController
@RequestMapping("/api/v1/shipping-charge")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Shipping", description = "Shipping charge calculation APIs")
public class ShippingController {

    private final ShippingCalculatorService shippingCalculatorService;
    private final WarehouseService warehouseService;
    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final GeoLocationService geoLocationService;

    /**
     * Get shipping charge from a warehouse to a customer.
     */
    @GetMapping
    @Operation(summary = "Calculate shipping charge", description = "Calculates the shipping charge based on warehouse location, "
            +
            "customer location, distance, and delivery speed. " +
            "Uses product weight if productId is provided, otherwise assumes 1kg.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated shipping charge", content = @Content(schema = @Schema(implementation = ShippingChargeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Warehouse or customer not found")
    })
    public ResponseEntity<ShippingChargeResponse> getShippingCharge(
            @Parameter(description = "Warehouse database ID", required = true, example = "1") @RequestParam @NotNull(message = "Warehouse ID is required") Long warehouseId,

            @Parameter(description = "Customer database ID", required = true, example = "1") @RequestParam @NotNull(message = "Customer ID is required") Long customerId,

            @Parameter(description = "Delivery speed: STANDARD or EXPRESS", required = true, example = "STANDARD") @RequestParam @NotBlank(message = "Delivery speed is required") String deliverySpeed,

            @Parameter(description = "Product ID for weight-based calculation", example = "1") @RequestParam(required = false) Long productId) {

        log.info("Request for shipping charge: warehouse={}, customer={}, speed={}, product={}",
                warehouseId, customerId, deliverySpeed, productId);

        // Parse and validate delivery speed
        DeliverySpeed speed = DeliverySpeed.fromString(deliverySpeed);

        // Calculate shipping charge
        BigDecimal charge = shippingCalculatorService.calculateShippingCharge(
                warehouseId, customerId, speed, productId);

        // Get additional details for response
        Warehouse warehouse = warehouseService.getWarehouseById(warehouseId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        double distanceKm = geoLocationService.calculateDistanceInKm(
                warehouse.getLocation(), customer.getLocation());

        double weightKg = productId != null ? productRepository.findById(productId)
                .map(Product::getChargeableWeight)
                .orElse(1.0) : 1.0;

        TransportMode transportMode = TransportMode.getByDistance(distanceKm);

        // Build response
        ShippingChargeResponse response = ShippingChargeResponse.builder()
                .shippingCharge(charge)
                .transportMode(transportMode)
                .deliverySpeed(speed)
                .distanceKm(Math.round(distanceKm * 100.0) / 100.0)
                .weightKg(weightKg)
                .currency("INR")
                .build();

        log.info("Calculated shipping charge: {} INR", charge);

        return ResponseEntity.ok(response);
    }

    /**
     * Calculate complete shipping charge from seller to customer via nearest
     * warehouse.
     */
    @PostMapping("/calculate")
    @Operation(summary = "Calculate complete shipping charge", description = "Calculates the complete shipping charge by first finding the nearest "
            +
            "warehouse to the seller, then calculating the shipping cost from that " +
            "warehouse to the customer location.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated shipping charge", content = @Content(schema = @Schema(implementation = ShippingCalculateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Seller, customer, or product not found")
    })
    public ResponseEntity<ShippingCalculateResponse> calculateShippingCharge(
            @Valid @RequestBody ShippingCalculateRequest request) {

        log.info("Request for complete shipping calculation: {}", request);

        // Parse delivery speed
        DeliverySpeed speed = DeliverySpeed.fromString(request.getDeliverySpeed());

        // Calculate shipping with warehouse lookup
        ShippingCalculatorService.ShippingCalculationResult result = shippingCalculatorService
                .calculateShippingForSellerAndCustomer(
                        request.getSellerId(),
                        request.getCustomerId(),
                        speed,
                        request.getProductId());

        Warehouse warehouse = result.warehouse();

        // Get seller for distance calculation
        Seller seller = sellerRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", request.getSellerId()));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        // Calculate distances
        double sellerToWarehouseKm = geoLocationService.calculateDistanceInKm(
                seller.getLocation(), warehouse.getLocation());
        double warehouseToCustomerKm = geoLocationService.calculateDistanceInKm(
                warehouse.getLocation(), customer.getLocation());

        double weightKg = request.getProductId() != null ? productRepository.findById(request.getProductId())
                .map(Product::getChargeableWeight)
                .orElse(1.0) : 1.0;

        TransportMode transportMode = TransportMode.getByDistance(warehouseToCustomerKm);

        // Build warehouse response
        NearestWarehouseResponse warehouseResponse = NearestWarehouseResponse.builder()
                .warehouseId(warehouse.getId())
                .warehouseCode(warehouse.getWarehouseCode())
                .warehouseName(warehouse.getName())
                .warehouseLocation(LocationResponse.builder()
                        .lat(warehouse.getLocation().getLatitude())
                        .lng(warehouse.getLocation().getLongitude())
                        .build())
                .distanceKm(Math.round(sellerToWarehouseKm * 100.0) / 100.0)
                .build();

        // Build complete response
        ShippingCalculateResponse response = ShippingCalculateResponse.builder()
                .shippingCharge(result.shippingCharge())
                .nearestWarehouse(warehouseResponse)
                .transportMode(transportMode)
                .deliverySpeed(speed)
                .distanceKm(Math.round(warehouseToCustomerKm * 100.0) / 100.0)
                .weightKg(weightKg)
                .currency("INR")
                .build();

        log.info("Complete shipping calculation result: {} INR via warehouse {}",
                result.shippingCharge(), warehouse.getWarehouseCode());

        return ResponseEntity.ok(response);
    }
}
