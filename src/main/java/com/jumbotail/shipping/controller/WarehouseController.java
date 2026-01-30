package com.jumbotail.shipping.controller;

import com.jumbotail.shipping.dto.response.LocationResponse;
import com.jumbotail.shipping.dto.response.NearestWarehouseResponse;
import com.jumbotail.shipping.entity.Seller;
import com.jumbotail.shipping.entity.Warehouse;
import com.jumbotail.shipping.exception.ResourceNotFoundException;
import com.jumbotail.shipping.repository.SellerRepository;
import com.jumbotail.shipping.service.GeoLocationService;
import com.jumbotail.shipping.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for warehouse-related operations.
 * Provides API to find the nearest warehouse for a seller.
 */
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Warehouse", description = "Warehouse management APIs")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final SellerRepository sellerRepository;
    private final GeoLocationService geoLocationService;

    /**
     * Get the nearest warehouse for a seller.
     * 
     * @param sellerId  the seller's database ID
     * @param productId optional product ID (for future use)
     * @return nearest warehouse details
     */
    @GetMapping("/nearest")
    @Operation(summary = "Find nearest warehouse", description = "Returns the nearest warehouse to a seller's location. "
            +
            "The seller drops off products at this warehouse for shipping to customers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found nearest warehouse", content = @Content(schema = @Schema(implementation = NearestWarehouseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Seller not found or no warehouses available")
    })
    public ResponseEntity<NearestWarehouseResponse> getNearestWarehouse(
            @Parameter(description = "Seller's database ID", required = true, example = "1") @RequestParam @NotNull(message = "Seller ID is required") Long sellerId,

            @Parameter(description = "Product ID (optional, for future use)", example = "1") @RequestParam(required = false) Long productId) {

        log.info("Request to find nearest warehouse for seller ID: {}", sellerId);

        // Get seller for distance calculation
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", sellerId));

        // Find nearest warehouse
        Warehouse warehouse = warehouseService.findNearestWarehouse(sellerId);

        // Calculate distance for response
        double distanceKm = 0.0;
        if (seller.getLocation() != null && warehouse.getLocation() != null) {
            distanceKm = geoLocationService.calculateDistanceInKm(
                    seller.getLocation(), warehouse.getLocation());
        }

        // Build response
        NearestWarehouseResponse response = NearestWarehouseResponse.builder()
                .warehouseId(warehouse.getId())
                .warehouseCode(warehouse.getWarehouseCode())
                .warehouseName(warehouse.getName())
                .warehouseLocation(LocationResponse.builder()
                        .lat(warehouse.getLocation().getLatitude())
                        .lng(warehouse.getLocation().getLongitude())
                        .build())
                .distanceKm(Math.round(distanceKm * 100.0) / 100.0) // Round to 2 decimals
                .build();

        log.info("Found nearest warehouse: {} at distance: {} km",
                warehouse.getWarehouseCode(), response.getDistanceKm());

        return ResponseEntity.ok(response);
    }
}
