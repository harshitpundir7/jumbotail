package com.jumbotail.shipping.controller;

import com.jumbotail.shipping.dto.request.CreateCustomerRequest;
import com.jumbotail.shipping.dto.request.CreateProductRequest;
import com.jumbotail.shipping.dto.request.CreateSellerRequest;
import com.jumbotail.shipping.dto.request.CreateWarehouseRequest;
import com.jumbotail.shipping.entity.Customer;
import com.jumbotail.shipping.entity.Product;
import com.jumbotail.shipping.entity.Seller;
import com.jumbotail.shipping.entity.Warehouse;
import com.jumbotail.shipping.entity.embeddable.GeoLocation;
import com.jumbotail.shipping.entity.embeddable.ProductDimensions;
import com.jumbotail.shipping.exception.ResourceNotFoundException;
import com.jumbotail.shipping.repository.CustomerRepository;
import com.jumbotail.shipping.repository.ProductRepository;
import com.jumbotail.shipping.repository.SellerRepository;
import com.jumbotail.shipping.repository.WarehouseRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for fetching and creating entity data for UI display.
 * Provides simplified endpoints to retrieve and add warehouses, sellers,
 * customers, and products.
 */
@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Data", description = "APIs for fetching and creating entity data for UI")
@CrossOrigin(origins = "*")
public class DataController {

    private final WarehouseRepository warehouseRepository;
    private final SellerRepository sellerRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    // ==================== GET ENDPOINTS ====================

    /**
     * Get all active warehouses for display.
     */
    @GetMapping("/warehouses")
    @Operation(summary = "Get all warehouses", description = "Returns all active warehouses with basic info")
    public ResponseEntity<List<Map<String, Object>>> getAllWarehouses() {
        List<Map<String, Object>> warehouses = warehouseRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapWarehouse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(warehouses);
    }

    /**
     * Get all active sellers for dropdown.
     */
    @GetMapping("/sellers")
    @Operation(summary = "Get all sellers", description = "Returns all active sellers for selection")
    public ResponseEntity<List<Map<String, Object>>> getAllSellers() {
        List<Map<String, Object>> sellers = sellerRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapSeller)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sellers);
    }

    /**
     * Get all active customers for dropdown.
     */
    @GetMapping("/customers")
    @Operation(summary = "Get all customers", description = "Returns all active customers for selection")
    public ResponseEntity<List<Map<String, Object>>> getAllCustomers() {
        List<Map<String, Object>> customers = customerRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapCustomer)
                .collect(Collectors.toList());
        return ResponseEntity.ok(customers);
    }

    /**
     * Get all active products for dropdown.
     */
    @GetMapping("/products")
    @Operation(summary = "Get all products", description = "Returns all active products for selection")
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        List<Map<String, Object>> products = productRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapProduct)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    // ==================== POST ENDPOINTS ====================

    /**
     * Create a new warehouse.
     */
    @PostMapping("/warehouses")
    @Operation(summary = "Create a new warehouse", description = "Adds a new warehouse to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Warehouse created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createWarehouse(@Valid @RequestBody CreateWarehouseRequest request) {
        log.info("Creating new warehouse: {}", request.getWarehouseCode());

        Warehouse warehouse = Warehouse.builder()
                .warehouseCode(request.getWarehouseCode())
                .name(request.getName())
                .location(GeoLocation.builder()
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .build())
                .address(request.getAddress())
                .pincode(request.getPincode())
                .city(request.getCity())
                .state(request.getState())
                .capacitySqFt(request.getCapacitySqFt())
                .utilizationPercent(0)
                .managerName(request.getManagerName())
                .contactPhone(request.getContactPhone())
                .isActive(true)
                .build();

        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Created warehouse with ID: {}", saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(mapWarehouse(saved));
    }

    /**
     * Create a new seller.
     */
    @PostMapping("/sellers")
    @Operation(summary = "Create a new seller", description = "Adds a new seller to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Seller created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createSeller(@Valid @RequestBody CreateSellerRequest request) {
        log.info("Creating new seller: {}", request.getSellerId());

        Seller seller = Seller.builder()
                .sellerId(request.getSellerId())
                .companyName(request.getCompanyName())
                .contactName(request.getContactName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .location(GeoLocation.builder()
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .build())
                .address(request.getAddress())
                .pincode(request.getPincode())
                .city(request.getCity())
                .state(request.getState())
                .gstNumber(request.getGstNumber())
                .panNumber(request.getPanNumber())
                .isActive(true)
                .build();

        Seller saved = sellerRepository.save(seller);
        log.info("Created seller with ID: {}", saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(mapSeller(saved));
    }

    /**
     * Create a new customer.
     */
    @PostMapping("/customers")
    @Operation(summary = "Create a new customer", description = "Adds a new customer to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        log.info("Creating new customer: {}", request.getCustomerId());

        Customer customer = Customer.builder()
                .customerId(request.getCustomerId())
                .storeName(request.getStoreName())
                .ownerName(request.getOwnerName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .location(GeoLocation.builder()
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .build())
                .address(request.getAddress())
                .pincode(request.getPincode())
                .city(request.getCity())
                .state(request.getState())
                .gstNumber(request.getGstNumber())
                .isActive(true)
                .build();

        Customer saved = customerRepository.save(customer);
        log.info("Created customer with ID: {}", saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(mapCustomer(saved));
    }

    /**
     * Create a new product.
     */
    @PostMapping("/products")
    @Operation(summary = "Create a new product", description = "Adds a new product to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Seller not found")
    })
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("Creating new product: {}", request.getProductId());

        Seller seller = sellerRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", request.getSellerId()));

        ProductDimensions dimensions = null;
        if (request.getLengthCm() != null && request.getWidthCm() != null && request.getHeightCm() != null) {
            dimensions = ProductDimensions.builder()
                    .lengthCm(request.getLengthCm())
                    .widthCm(request.getWidthCm())
                    .heightCm(request.getHeightCm())
                    .build();
        }

        Product product = Product.builder()
                .productId(request.getProductId())
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .sellingPrice(request.getSellingPrice())
                .mrp(request.getMrp())
                .weightInKg(request.getWeightInKg())
                .dimensions(dimensions)
                .seller(seller)
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .isActive(true)
                .build();

        Product saved = productRepository.save(product);
        log.info("Created product with ID: {}", saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(mapProduct(saved));
    }

    // ==================== MAPPING METHODS ====================

    private Map<String, Object> mapWarehouse(Warehouse w) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", w.getId());
        map.put("code", w.getWarehouseCode());
        map.put("name", w.getName());
        map.put("city", w.getCity());
        map.put("state", w.getState());
        map.put("capacity", w.getCapacitySqFt());
        map.put("utilization", w.getUtilizationPercent());
        map.put("lat", w.getLocation().getLatitude());
        map.put("lng", w.getLocation().getLongitude());
        return map;
    }

    private Map<String, Object> mapSeller(Seller s) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", s.getId());
        map.put("sellerId", s.getSellerId());
        map.put("name", s.getCompanyName());
        map.put("city", s.getCity());
        map.put("state", s.getState());
        return map;
    }

    private Map<String, Object> mapCustomer(Customer c) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", c.getId());
        map.put("customerId", c.getCustomerId());
        map.put("name", c.getStoreName());
        map.put("city", c.getCity());
        map.put("state", c.getState());
        return map;
    }

    private Map<String, Object> mapProduct(Product p) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", p.getId());
        map.put("productId", p.getProductId());
        map.put("name", p.getName());
        map.put("category", p.getCategory());
        map.put("weight", p.getWeightInKg());
        map.put("price", p.getSellingPrice());
        return map;
    }
}
