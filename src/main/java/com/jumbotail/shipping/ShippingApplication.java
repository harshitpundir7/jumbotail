package com.jumbotail.shipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main entry point for the Shipping Charge Estimator application.
 * 
 * This application provides APIs to calculate shipping charges for a B2B
 * e-commerce marketplace serving Kirana stores across India.
 * 
 * @author Jumbotail Engineering
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
public class ShippingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShippingApplication.class, args);
    }
}
