package com.jumbotail.shipping.service;

import com.jumbotail.shipping.entity.Seller;
import com.jumbotail.shipping.entity.Warehouse;
import com.jumbotail.shipping.entity.embeddable.GeoLocation;
import com.jumbotail.shipping.exception.ResourceNotFoundException;
import com.jumbotail.shipping.repository.SellerRepository;
import com.jumbotail.shipping.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WarehouseService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WarehouseService Tests")
class WarehouseServiceTest {

        @Mock
        private WarehouseRepository warehouseRepository;

        @Mock
        private SellerRepository sellerRepository;

        @Mock
        private GeoLocationService geoLocationService;

        @InjectMocks
        private WarehouseService warehouseService;

        private Seller testSeller;
        private Warehouse bangaloreWarehouse;
        private Warehouse mumbaiWarehouse;
        private Warehouse delhiWarehouse;

        @BeforeEach
        void setUp() {
                // Setup test seller in Hyderabad
                testSeller = Seller.builder()
                                .id(1L)
                                .sellerId("SELLER-001")
                                .companyName("Test Seller")
                                .location(GeoLocation.builder()
                                                .latitude(17.3850)
                                                .longitude(78.4867)
                                                .build())
                                .build();

                // Setup warehouses
                bangaloreWarehouse = Warehouse.builder()
                                .id(1L)
                                .warehouseCode("BLR_WH_01")
                                .name("Bangalore Warehouse")
                                .location(GeoLocation.builder()
                                                .latitude(12.9716)
                                                .longitude(77.5946)
                                                .build())
                                .isActive(true)
                                .build();

                mumbaiWarehouse = Warehouse.builder()
                                .id(2L)
                                .warehouseCode("MUM_WH_01")
                                .name("Mumbai Warehouse")
                                .location(GeoLocation.builder()
                                                .latitude(19.0760)
                                                .longitude(72.8777)
                                                .build())
                                .isActive(true)
                                .build();

                delhiWarehouse = Warehouse.builder()
                                .id(3L)
                                .warehouseCode("DEL_WH_01")
                                .name("Delhi Warehouse")
                                .location(GeoLocation.builder()
                                                .latitude(28.7041)
                                                .longitude(77.1025)
                                                .build())
                                .isActive(true)
                                .build();
        }

        @Nested
        @DisplayName("Find Nearest Warehouse Tests")
        class FindNearestWarehouseTests {

                @Test
                @DisplayName("Should find the nearest warehouse to seller")
                void shouldFindNearestWarehouse() {
                        // Arrange
                        List<Warehouse> warehouses = Arrays.asList(bangaloreWarehouse, mumbaiWarehouse, delhiWarehouse);

                        when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));
                        when(warehouseRepository.findByIsActiveTrue()).thenReturn(warehouses);

                        // Hyderabad to Bangalore ~500km, to Mumbai ~710km, to Delhi ~1260km
                        when(geoLocationService.calculateDistanceInKm(testSeller.getLocation(),
                                        bangaloreWarehouse.getLocation()))
                                        .thenReturn(500.0);
                        when(geoLocationService.calculateDistanceInKm(testSeller.getLocation(),
                                        mumbaiWarehouse.getLocation()))
                                        .thenReturn(710.0);
                        when(geoLocationService.calculateDistanceInKm(testSeller.getLocation(),
                                        delhiWarehouse.getLocation()))
                                        .thenReturn(1260.0);

                        // Act
                        Warehouse nearest = warehouseService.findNearestWarehouse(1L);

                        // Assert
                        assertThat(nearest).isEqualTo(bangaloreWarehouse);
                        verify(geoLocationService, atLeast(3)).calculateDistanceInKm(any(), any());
                }

                @Test
                @DisplayName("Should throw exception when seller not found")
                void shouldThrowExceptionWhenSellerNotFound() {
                        // Arrange
                        when(sellerRepository.findById(999L)).thenReturn(Optional.empty());

                        // Act & Assert
                        assertThatThrownBy(() -> warehouseService.findNearestWarehouse(999L))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("Seller");
                }

                @Test
                @DisplayName("Should throw exception when seller has no location")
                void shouldThrowExceptionWhenSellerHasNoLocation() {
                        // Arrange
                        Seller sellerWithoutLocation = Seller.builder()
                                        .id(2L)
                                        .sellerId("SELLER-002")
                                        .companyName("No Location Seller")
                                        .location(null)
                                        .build();

                        when(sellerRepository.findById(2L)).thenReturn(Optional.of(sellerWithoutLocation));

                        // Act & Assert
                        assertThatThrownBy(() -> warehouseService.findNearestWarehouse(2L))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("location");
                }

                @Test
                @DisplayName("Should throw exception when no active warehouses available")
                void shouldThrowExceptionWhenNoActiveWarehouses() {
                        // Arrange
                        when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));
                        when(warehouseRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());

                        // Act & Assert
                        assertThatThrownBy(() -> warehouseService.findNearestWarehouse(1L))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("No active warehouses");
                }
        }

        @Nested
        @DisplayName("Get Warehouse By ID Tests")
        class GetWarehouseByIdTests {

                @Test
                @DisplayName("Should return warehouse when found")
                void shouldReturnWarehouseWhenFound() {
                        // Arrange
                        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(bangaloreWarehouse));

                        // Act
                        Warehouse warehouse = warehouseService.getWarehouseById(1L);

                        // Assert
                        assertThat(warehouse).isEqualTo(bangaloreWarehouse);
                }

                @Test
                @DisplayName("Should throw exception when warehouse not found")
                void shouldThrowExceptionWhenWarehouseNotFound() {
                        // Arrange
                        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

                        // Act & Assert
                        assertThatThrownBy(() -> warehouseService.getWarehouseById(999L))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("Warehouse");
                }
        }

        @Nested
        @DisplayName("Get All Active Warehouses Tests")
        class GetAllActiveWarehousesTests {

                @Test
                @DisplayName("Should return all active warehouses")
                void shouldReturnAllActiveWarehouses() {
                        // Arrange
                        List<Warehouse> warehouses = Arrays.asList(bangaloreWarehouse, mumbaiWarehouse);
                        when(warehouseRepository.findByIsActiveTrue()).thenReturn(warehouses);

                        // Act
                        List<Warehouse> result = warehouseService.getAllActiveWarehouses();

                        // Assert
                        assertThat(result).hasSize(2);
                        assertThat(result).containsExactlyInAnyOrder(bangaloreWarehouse, mumbaiWarehouse);
                }

                @Test
                @DisplayName("Should return empty list when no active warehouses")
                void shouldReturnEmptyListWhenNoActiveWarehouses() {
                        // Arrange
                        when(warehouseRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());

                        // Act
                        List<Warehouse> result = warehouseService.getAllActiveWarehouses();

                        // Assert
                        assertThat(result).isEmpty();
                }
        }
}
