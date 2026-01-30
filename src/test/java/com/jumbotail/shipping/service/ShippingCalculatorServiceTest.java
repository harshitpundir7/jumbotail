package com.jumbotail.shipping.service;

import com.jumbotail.shipping.entity.Customer;
import com.jumbotail.shipping.entity.Product;
import com.jumbotail.shipping.entity.Warehouse;
import com.jumbotail.shipping.entity.embeddable.GeoLocation;
import com.jumbotail.shipping.enums.DeliverySpeed;
import com.jumbotail.shipping.enums.TransportMode;
import com.jumbotail.shipping.exception.ResourceNotFoundException;
import com.jumbotail.shipping.repository.CustomerRepository;
import com.jumbotail.shipping.repository.ProductRepository;
import com.jumbotail.shipping.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ShippingCalculatorService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ShippingCalculatorService Tests")
class ShippingCalculatorServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private GeoLocationService geoLocationService;

    @InjectMocks
    private ShippingCalculatorService shippingCalculatorService;

    private Warehouse testWarehouse;
    private Customer testCustomer;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Setup test warehouse
        testWarehouse = Warehouse.builder()
                .id(1L)
                .warehouseCode("BLR_WH_01")
                .name("Bangalore Warehouse")
                .location(GeoLocation.builder()
                        .latitude(12.9716)
                        .longitude(77.5946)
                        .build())
                .build();

        // Setup test customer
        testCustomer = Customer.builder()
                .id(1L)
                .customerId("CUST-001")
                .storeName("Test Store")
                .location(GeoLocation.builder()
                        .latitude(19.0760)
                        .longitude(72.8777)
                        .build())
                .build();

        // Setup test product
        testProduct = Product.builder()
                .id(1L)
                .productId("PROD-001")
                .name("Test Product")
                .weightInKg(5.0)
                .build();
    }

    @Nested
    @DisplayName("Calculate Total Charge Tests")
    class CalculateTotalChargeTests {

        @Test
        @DisplayName("Should calculate charge correctly for short distance (MiniVan)")
        void shouldCalculateChargeForShortDistance() {
            // Arrange - 50 km, 5 kg, STANDARD delivery
            double distanceKm = 50.0;
            double weightKg = 5.0;
            DeliverySpeed speed = DeliverySpeed.STANDARD;

            // Expected: Transport = 3 * 50 * 5 = 750, Delivery = 10 + 0 = 10, Total = 760
            // Act
            BigDecimal charge = shippingCalculatorService.calculateTotalCharge(distanceKm, weightKg, speed);

            // Assert
            assertThat(charge).isEqualByComparingTo(new BigDecimal("760.00"));
        }

        @Test
        @DisplayName("Should calculate charge correctly for medium distance (Truck)")
        void shouldCalculateChargeForMediumDistance() {
            // Arrange - 250 km, 10 kg, STANDARD delivery
            double distanceKm = 250.0;
            double weightKg = 10.0;
            DeliverySpeed speed = DeliverySpeed.STANDARD;

            // Expected: Transport = 2 * 250 * 10 = 5000, Delivery = 10 + 0 = 10, Total =
            // 5010
            // Act
            BigDecimal charge = shippingCalculatorService.calculateTotalCharge(distanceKm, weightKg, speed);

            // Assert
            assertThat(charge).isEqualByComparingTo(new BigDecimal("5010.00"));
        }

        @Test
        @DisplayName("Should calculate charge correctly for long distance (Aeroplane)")
        void shouldCalculateChargeForLongDistance() {
            // Arrange - 800 km, 2 kg, STANDARD delivery
            double distanceKm = 800.0;
            double weightKg = 2.0;
            DeliverySpeed speed = DeliverySpeed.STANDARD;

            // Expected: Transport = 1 * 800 * 2 = 1600, Delivery = 10 + 0 = 10, Total =
            // 1610
            // Act
            BigDecimal charge = shippingCalculatorService.calculateTotalCharge(distanceKm, weightKg, speed);

            // Assert
            assertThat(charge).isEqualByComparingTo(new BigDecimal("1610.00"));
        }

        @Test
        @DisplayName("Should add express charge correctly")
        void shouldAddExpressChargeCorrectly() {
            // Arrange - 50 km, 5 kg, EXPRESS delivery
            double distanceKm = 50.0;
            double weightKg = 5.0;
            DeliverySpeed speed = DeliverySpeed.EXPRESS;

            // Expected: Transport = 3 * 50 * 5 = 750, Delivery = 10 + (1.2 * 5) = 16, Total
            // = 766
            // Act
            BigDecimal charge = shippingCalculatorService.calculateTotalCharge(distanceKm, weightKg, speed);

            // Assert
            assertThat(charge).isEqualByComparingTo(new BigDecimal("766.00"));
        }

        @Test
        @DisplayName("Should handle boundary distance for transport mode selection")
        void shouldHandleBoundaryDistanceCorrectly() {
            // At exactly 100 km, should use TRUCK (100+)
            BigDecimal chargeAt100 = shippingCalculatorService.calculateTotalCharge(100.0, 1.0, DeliverySpeed.STANDARD);
            // Transport = 2 * 100 * 1 = 200, Delivery = 10, Total = 210

            // At 99 km, should use MINI_VAN
            BigDecimal chargeAt99 = shippingCalculatorService.calculateTotalCharge(99.0, 1.0, DeliverySpeed.STANDARD);
            // Transport = 3 * 99 * 1 = 297, Delivery = 10, Total = 307

            assertThat(chargeAt100).isEqualByComparingTo(new BigDecimal("210.00"));
            assertThat(chargeAt99).isEqualByComparingTo(new BigDecimal("307.00"));
        }
    }

    @Nested
    @DisplayName("Calculate Shipping Charge Tests")
    class CalculateShippingChargeTests {

        @Test
        @DisplayName("Should calculate shipping charge with product weight")
        void shouldCalculateShippingChargeWithProduct() {
            // Arrange
            when(warehouseService.getWarehouseById(1L)).thenReturn(testWarehouse);
            when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
            when(geoLocationService.calculateDistanceInKm(any(), any())).thenReturn(250.0);

            // Act
            BigDecimal charge = shippingCalculatorService.calculateShippingCharge(
                    1L, 1L, DeliverySpeed.STANDARD, 1L);

            // Assert
            // Transport = 2 * 250 * 5 = 2500, Delivery = 10, Total = 2510
            assertThat(charge).isEqualByComparingTo(new BigDecimal("2510.00"));
            verify(productRepository).findById(1L);
        }

        @Test
        @DisplayName("Should use default weight when product not specified")
        void shouldUseDefaultWeightWhenProductNotSpecified() {
            // Arrange
            when(warehouseService.getWarehouseById(1L)).thenReturn(testWarehouse);
            when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
            when(geoLocationService.calculateDistanceInKm(any(), any())).thenReturn(50.0);

            // Act
            BigDecimal charge = shippingCalculatorService.calculateShippingCharge(
                    1L, 1L, DeliverySpeed.STANDARD, null);

            // Assert
            // Transport = 3 * 50 * 1 (default) = 150, Delivery = 10, Total = 160
            assertThat(charge).isEqualByComparingTo(new BigDecimal("160.00"));
            verify(productRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should throw exception when customer not found")
        void shouldThrowExceptionWhenCustomerNotFound() {
            // Arrange
            when(warehouseService.getWarehouseById(1L)).thenReturn(testWarehouse);
            when(customerRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> shippingCalculatorService.calculateShippingCharge(
                    1L, 999L, DeliverySpeed.STANDARD, null))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Customer");
        }

        @Test
        @DisplayName("Should throw exception when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            // Arrange
            when(warehouseService.getWarehouseById(1L)).thenReturn(testWarehouse);
            when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> shippingCalculatorService.calculateShippingCharge(
                    1L, 1L, DeliverySpeed.STANDARD, 999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product");
        }
    }

    @Nested
    @DisplayName("Transport Mode Selection Tests")
    class TransportModeSelectionTests {

        @Test
        @DisplayName("Should select AEROPLANE for 500+ km")
        void shouldSelectAeroplaneForLongDistance() {
            TransportMode mode = TransportMode.getByDistance(500.0);
            assertThat(mode).isEqualTo(TransportMode.AEROPLANE);
        }

        @Test
        @DisplayName("Should select TRUCK for 100-499 km")
        void shouldSelectTruckForMediumDistance() {
            TransportMode mode = TransportMode.getByDistance(250.0);
            assertThat(mode).isEqualTo(TransportMode.TRUCK);
        }

        @Test
        @DisplayName("Should select MINI_VAN for 0-99 km")
        void shouldSelectMiniVanForShortDistance() {
            TransportMode mode = TransportMode.getByDistance(50.0);
            assertThat(mode).isEqualTo(TransportMode.MINI_VAN);
        }
    }
}
