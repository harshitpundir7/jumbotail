package com.jumbotail.shipping.entity;

import com.jumbotail.shipping.entity.embeddable.GeoLocation;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a Customer (Kirana store) in the B2B marketplace.
 * Stores complete customer details including location for shipping
 * calculations.
 */
@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_customer_id", columnList = "customerId", unique = true),
        @Index(name = "idx_customer_pincode", columnList = "pincode"),
        @Index(name = "idx_customer_city", columnList = "city")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Business identifier for the customer (e.g., "CUST-123").
     */
    @NotBlank(message = "Customer ID is required")
    @Size(max = 50, message = "Customer ID cannot exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String customerId;

    /**
     * Name of the Kirana store.
     */
    @NotBlank(message = "Store name is required")
    @Size(max = 200, message = "Store name cannot exceed 200 characters")
    @Column(nullable = false, length = 200)
    private String storeName;

    /**
     * Name of the store owner.
     */
    @Size(max = 100, message = "Owner name cannot exceed 100 characters")
    @Column(length = 100)
    private String ownerName;

    /**
     * Contact phone number.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    @Column(nullable = false, length = 15)
    private String phoneNumber;

    /**
     * Email address for communications.
     */
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(length = 100)
    private String email;

    /**
     * Geographic location (latitude, longitude) for distance calculations.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "location_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "location_longitude"))
    })
    @Valid
    private GeoLocation location;

    /**
     * Full street address.
     */
    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String address;

    /**
     * Postal pincode for the delivery address.
     */
    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid Indian pincode")
    @Column(nullable = false, length = 6)
    private String pincode;

    /**
     * City name.
     */
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String city;

    /**
     * State name.
     */
    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String state;

    /**
     * GST number for B2B compliance.
     */
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Invalid GST number format")
    @Column(length = 15)
    private String gstNumber;

    /**
     * Whether the customer is active in the system.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    /**
     * Timestamp of record creation.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of last update.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
