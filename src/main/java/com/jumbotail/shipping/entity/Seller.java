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
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a Seller who lists products on the marketplace.
 * Sellers can be located anywhere in India and drop items at the nearest
 * warehouse.
 */
@Entity
@Table(name = "sellers", indexes = {
        @Index(name = "idx_seller_seller_id", columnList = "sellerId", unique = true),
        @Index(name = "idx_seller_city", columnList = "city")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Business identifier for the seller (e.g., "SELLER-001").
     */
    @NotBlank(message = "Seller ID is required")
    @Size(max = 50, message = "Seller ID cannot exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String sellerId;

    /**
     * Company/business name of the seller.
     */
    @NotBlank(message = "Company name is required")
    @Size(max = 200, message = "Company name cannot exceed 200 characters")
    @Column(nullable = false, length = 200)
    private String companyName;

    /**
     * Primary contact person name.
     */
    @Size(max = 100, message = "Contact name cannot exceed 100 characters")
    @Column(length = 100)
    private String contactName;

    /**
     * Contact phone number.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    @Column(nullable = false, length = 15)
    private String phoneNumber;

    /**
     * Business email address.
     */
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(length = 100)
    private String email;

    /**
     * Geographic location for finding nearest warehouse.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "location_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "location_longitude"))
    })
    @Valid
    private GeoLocation location;

    /**
     * Full business address.
     */
    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String address;

    /**
     * Postal pincode.
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
     * PAN number for tax compliance.
     */
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number format")
    @Column(length = 10)
    private String panNumber;

    /**
     * Products listed by this seller.
     */
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Product> products = new ArrayList<>();

    /**
     * Whether the seller is active in the system.
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

    /**
     * Helper method to add a product to this seller.
     */
    public void addProduct(Product product) {
        products.add(product);
        product.setSeller(this);
    }

    /**
     * Helper method to remove a product from this seller.
     */
    public void removeProduct(Product product) {
        products.remove(product);
        product.setSeller(null);
    }
}
