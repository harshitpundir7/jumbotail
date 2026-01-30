package com.jumbotail.shipping.repository;

import com.jumbotail.shipping.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Customer entity CRUD operations.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customer by business identifier.
     */
    Optional<Customer> findByCustomerId(String customerId);

    /**
     * Find customer by phone number.
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /**
     * Check if customer exists by business identifier.
     */
    boolean existsByCustomerId(String customerId);
}
