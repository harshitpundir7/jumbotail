package com.jumbotail.shipping.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumbotail.shipping.dto.request.ShippingCalculateRequest;
import com.jumbotail.shipping.entity.Customer;
import com.jumbotail.shipping.entity.Product;
import com.jumbotail.shipping.entity.Seller;
import com.jumbotail.shipping.entity.Warehouse;
import com.jumbotail.shipping.entity.embeddable.GeoLocation;
import com.jumbotail.shipping.repository.CustomerRepository;
import com.jumbotail.shipping.repository.ProductRepository;
import com.jumbotail.shipping.repository.SellerRepository;
import com.jumbotail.shipping.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for shipping APIs.
 * Tests the complete flow from controller to database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Shipping API Integration Tests")
class ShippingApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Warehouse testWarehouse;
    private Seller testSeller;
    private Customer testCustomer;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Create test warehouse
        testWarehouse = warehouseRepository.save(Warehouse.builder()
                .warehouseCode("TEST_WH_01")
                .name("Test Warehouse")
                .location(GeoLocation.builder()
                        .latitude(12.9716)
                        .longitude(77.5946)
                        .build())
                .address("Test Address")
                .pincode("560001")
                .city("Bangalore")
                .state("Karnataka")
                .capacitySqFt(10000)
                .isActive(true)
                .build());

        // Create test seller
        testSeller = sellerRepository.save(Seller.builder()
                .sellerId("TEST-SELLER-001")
                .companyName("Test Seller Co")
                .phoneNumber("9876543210")
                .location(GeoLocation.builder()
                        .latitude(12.9352)
                        .longitude(77.6245)
                        .build())
                .address("Seller Address")
                .pincode("560038")
                .city("Bangalore")
                .state("Karnataka")
                .isActive(true)
                .build());

        // Create test customer
        testCustomer = customerRepository.save(Customer.builder()
                .customerId("TEST-CUST-001")
                .storeName("Test Kirana Store")
                .phoneNumber("9845123456")
                .location(GeoLocation.builder()
                        .latitude(19.0760)
                        .longitude(72.8777)
                        .build())
                .address("Customer Address")
                .pincode("400001")
                .city("Mumbai")
                .state("Maharashtra")
                .isActive(true)
                .build());

        // Create test product
        testProduct = productRepository.save(Product.builder()
                .productId("TEST-PROD-001")
                .name("Test Product")
                .category("Test Category")
                .sellingPrice(new java.math.BigDecimal("100.00"))
                .weightInKg(5.0)
                .seller(testSeller)
                .isActive(true)
                .stockQuantity(100)
                .build());
    }

    @Nested
    @DisplayName("GET /api/v1/warehouse/nearest")
    class NearestWarehouseEndpointTests {

        @Test
        @DisplayName("Should return nearest warehouse for valid seller")
        void shouldReturnNearestWarehouse() throws Exception {
            mockMvc.perform(get("/api/v1/warehouse/nearest")
                    .param("sellerId", testSeller.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.warehouseId").value(testWarehouse.getId()))
                    .andExpect(jsonPath("$.warehouseCode").value("TEST_WH_01"))
                    .andExpect(jsonPath("$.warehouseLocation.lat").isNumber())
                    .andExpect(jsonPath("$.warehouseLocation.lng").isNumber())
                    .andExpect(jsonPath("$.distanceKm").isNumber());
        }

        @Test
        @DisplayName("Should return 404 when seller not found")
        void shouldReturn404WhenSellerNotFound() throws Exception {
            mockMvc.perform(get("/api/v1/warehouse/nearest")
                    .param("sellerId", "99999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("Seller")));
        }

        @Test
        @DisplayName("Should return 400 when sellerId is missing")
        void shouldReturn400WhenSellerIdMissing() throws Exception {
            mockMvc.perform(get("/api/v1/warehouse/nearest"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("sellerId")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/shipping-charge")
    class ShippingChargeEndpointTests {

        @Test
        @DisplayName("Should calculate shipping charge for valid request")
        void shouldCalculateShippingCharge() throws Exception {
            mockMvc.perform(get("/api/v1/shipping-charge")
                    .param("warehouseId", testWarehouse.getId().toString())
                    .param("customerId", testCustomer.getId().toString())
                    .param("deliverySpeed", "STANDARD"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.shippingCharge").isNumber())
                    .andExpect(jsonPath("$.transportMode").exists())
                    .andExpect(jsonPath("$.deliverySpeed").value("STANDARD"))
                    .andExpect(jsonPath("$.distanceKm").isNumber())
                    .andExpect(jsonPath("$.currency").value("INR"));
        }

        @Test
        @DisplayName("Should calculate shipping charge with product")
        void shouldCalculateShippingChargeWithProduct() throws Exception {
            mockMvc.perform(get("/api/v1/shipping-charge")
                    .param("warehouseId", testWarehouse.getId().toString())
                    .param("customerId", testCustomer.getId().toString())
                    .param("deliverySpeed", "EXPRESS")
                    .param("productId", testProduct.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.shippingCharge").isNumber())
                    .andExpect(jsonPath("$.deliverySpeed").value("EXPRESS"))
                    .andExpect(jsonPath("$.weightKg").value(5.0));
        }

        @Test
        @DisplayName("Should return 400 for invalid delivery speed")
        void shouldReturn400ForInvalidDeliverySpeed() throws Exception {
            mockMvc.perform(get("/api/v1/shipping-charge")
                    .param("warehouseId", testWarehouse.getId().toString())
                    .param("customerId", testCustomer.getId().toString())
                    .param("deliverySpeed", "INVALID"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("delivery speed")));
        }

        @Test
        @DisplayName("Should return 404 when warehouse not found")
        void shouldReturn404WhenWarehouseNotFound() throws Exception {
            mockMvc.perform(get("/api/v1/shipping-charge")
                    .param("warehouseId", "99999")
                    .param("customerId", testCustomer.getId().toString())
                    .param("deliverySpeed", "STANDARD"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("Warehouse")));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/shipping-charge/calculate")
    class CalculateShippingEndpointTests {

        @Test
        @DisplayName("Should calculate complete shipping for valid request")
        void shouldCalculateCompleteShipping() throws Exception {
            ShippingCalculateRequest request = ShippingCalculateRequest.builder()
                    .sellerId(testSeller.getId())
                    .customerId(testCustomer.getId())
                    .deliverySpeed("EXPRESS")
                    .build();

            mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.shippingCharge").isNumber())
                    .andExpect(jsonPath("$.nearestWarehouse.warehouseId").value(testWarehouse.getId()))
                    .andExpect(jsonPath("$.deliverySpeed").value("EXPRESS"))
                    .andExpect(jsonPath("$.currency").value("INR"));
        }

        @Test
        @DisplayName("Should calculate shipping with product weight")
        void shouldCalculateShippingWithProduct() throws Exception {
            ShippingCalculateRequest request = ShippingCalculateRequest.builder()
                    .sellerId(testSeller.getId())
                    .customerId(testCustomer.getId())
                    .productId(testProduct.getId())
                    .deliverySpeed("STANDARD")
                    .build();

            mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.weightKg").value(5.0));
        }

        @Test
        @DisplayName("Should return 400 for missing required fields")
        void shouldReturn400ForMissingFields() throws Exception {
            ShippingCalculateRequest request = ShippingCalculateRequest.builder()
                    .sellerId(testSeller.getId())
                    // Missing customerId and deliverySpeed
                    .build();

            mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors").exists());
        }

        @Test
        @DisplayName("Should return 400 for malformed JSON")
        void shouldReturn400ForMalformedJson() throws Exception {
            mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ invalid json }"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Malformed JSON")));
        }
    }
}
