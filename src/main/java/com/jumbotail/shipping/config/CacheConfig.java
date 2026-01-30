package com.jumbotail.shipping.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration using Caffeine for in-memory caching.
 * Provides fast response times for frequently accessed data.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Creates a Caffeine cache manager with predefined cache settings.
     * 
     * Cache regions:
     * - nearestWarehouse: Caches nearest warehouse lookups (10 min TTL)
     * - shippingCharges: Caches shipping charge calculations (5 min TTL)
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(500)
                .recordStats());
        return cacheManager;
    }
}
