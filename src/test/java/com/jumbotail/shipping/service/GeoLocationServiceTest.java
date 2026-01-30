package com.jumbotail.shipping.service;

import com.jumbotail.shipping.entity.embeddable.GeoLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for GeoLocationService.
 * Tests the Haversine formula implementation for distance calculations.
 */
@DisplayName("GeoLocationService Tests")
class GeoLocationServiceTest {

        private GeoLocationService geoLocationService;

        @BeforeEach
        void setUp() {
                geoLocationService = new GeoLocationService();
        }

        @Nested
        @DisplayName("Distance Calculation Tests")
        class DistanceCalculationTests {

                @Test
                @DisplayName("Should calculate distance between Bangalore and Mumbai correctly")
                void shouldCalculateDistanceBangaloreToMumbai() {
                        // Arrange
                        GeoLocation bangalore = GeoLocation.builder()
                                        .latitude(12.9716)
                                        .longitude(77.5946)
                                        .build();

                        GeoLocation mumbai = GeoLocation.builder()
                                        .latitude(19.0760)
                                        .longitude(72.8777)
                                        .build();

                        // Act
                        double distance = geoLocationService.calculateDistanceInKm(bangalore, mumbai);

                        // Assert - Actual distance is approximately 845 km
                        assertThat(distance).isBetween(840.0, 850.0);
                }

                @Test
                @DisplayName("Should calculate distance between Delhi and Kolkata correctly")
                void shouldCalculateDistanceDelhiToKolkata() {
                        // Arrange
                        GeoLocation delhi = GeoLocation.builder()
                                        .latitude(28.7041)
                                        .longitude(77.1025)
                                        .build();

                        GeoLocation kolkata = GeoLocation.builder()
                                        .latitude(22.5726)
                                        .longitude(88.3639)
                                        .build();

                        // Act
                        double distance = geoLocationService.calculateDistanceInKm(delhi, kolkata);

                        // Assert - Actual distance is approximately 1305-1320 km
                        assertThat(distance).isBetween(1300.0, 1320.0);
                }

                @Test
                @DisplayName("Should return zero for same location")
                void shouldReturnZeroForSameLocation() {
                        // Arrange
                        GeoLocation location = GeoLocation.builder()
                                        .latitude(12.9716)
                                        .longitude(77.5946)
                                        .build();

                        // Act
                        double distance = geoLocationService.calculateDistanceInKm(location, location);

                        // Assert
                        assertThat(distance).isEqualTo(0.0);
                }

                @Test
                @DisplayName("Should handle short distances (within city)")
                void shouldHandleShortDistances() {
                        // Arrange - Two points in Bangalore approximately 5 km apart
                        GeoLocation point1 = GeoLocation.builder()
                                        .latitude(12.9716)
                                        .longitude(77.5946)
                                        .build();

                        GeoLocation point2 = GeoLocation.builder()
                                        .latitude(12.9352)
                                        .longitude(77.6245)
                                        .build();

                        // Act
                        double distance = geoLocationService.calculateDistanceInKm(point1, point2);

                        // Assert - Should be around 5 km
                        assertThat(distance).isBetween(4.0, 6.0);
                }

                @Test
                @DisplayName("Should throw exception when from location is null")
                void shouldThrowExceptionWhenFromLocationIsNull() {
                        // Arrange
                        GeoLocation to = GeoLocation.builder()
                                        .latitude(12.9716)
                                        .longitude(77.5946)
                                        .build();

                        // Act & Assert
                        assertThatThrownBy(() -> geoLocationService.calculateDistanceInKm(null, to))
                                        .isInstanceOf(IllegalArgumentException.class)
                                        .hasMessageContaining("Both locations must be provided");
                }

                @Test
                @DisplayName("Should throw exception when to location is null")
                void shouldThrowExceptionWhenToLocationIsNull() {
                        // Arrange
                        GeoLocation from = GeoLocation.builder()
                                        .latitude(12.9716)
                                        .longitude(77.5946)
                                        .build();

                        // Act & Assert
                        assertThatThrownBy(() -> geoLocationService.calculateDistanceInKm(from, null))
                                        .isInstanceOf(IllegalArgumentException.class)
                                        .hasMessageContaining("Both locations must be provided");
                }

                @Test
                @DisplayName("Should throw exception when coordinates are null")
                void shouldThrowExceptionWhenCoordinatesAreNull() {
                        // Arrange
                        GeoLocation from = GeoLocation.builder()
                                        .latitude(null)
                                        .longitude(77.5946)
                                        .build();

                        GeoLocation to = GeoLocation.builder()
                                        .latitude(19.0760)
                                        .longitude(72.8777)
                                        .build();

                        // Act & Assert
                        assertThatThrownBy(() -> geoLocationService.calculateDistanceInKm(from, to))
                                        .isInstanceOf(IllegalArgumentException.class)
                                        .hasMessageContaining("coordinates cannot be null");
                }
        }

        @Nested
        @DisplayName("Within Distance Tests")
        class WithinDistanceTests {

                @Test
                @DisplayName("Should return true when within distance threshold")
                void shouldReturnTrueWhenWithinDistance() {
                        // Arrange
                        GeoLocation bangalore = GeoLocation.builder()
                                        .latitude(12.9716)
                                        .longitude(77.5946)
                                        .build();

                        GeoLocation chennai = GeoLocation.builder()
                                        .latitude(13.0827)
                                        .longitude(80.2707)
                                        .build();

                        // Act - Chennai is about 290 km from Bangalore
                        boolean isWithin = geoLocationService.isWithinDistance(bangalore, chennai, 300.0);

                        // Assert
                        assertThat(isWithin).isTrue();
                }

                @Test
                @DisplayName("Should return false when beyond distance threshold")
                void shouldReturnFalseWhenBeyondDistance() {
                        // Arrange
                        GeoLocation bangalore = GeoLocation.builder()
                                        .latitude(12.9716)
                                        .longitude(77.5946)
                                        .build();

                        GeoLocation mumbai = GeoLocation.builder()
                                        .latitude(19.0760)
                                        .longitude(72.8777)
                                        .build();

                        // Act - Mumbai is about 845 km from Bangalore
                        boolean isWithin = geoLocationService.isWithinDistance(bangalore, mumbai, 500.0);

                        // Assert
                        assertThat(isWithin).isFalse();
                }
        }

        @Nested
        @DisplayName("Bearing Calculation Tests")
        class BearingCalculationTests {

                @Test
                @DisplayName("Should calculate bearing correctly")
                void shouldCalculateBearingCorrectly() {
                        // Arrange
                        GeoLocation bangalore = GeoLocation.builder()
                                        .latitude(12.9716)
                                        .longitude(77.5946)
                                        .build();

                        GeoLocation mumbai = GeoLocation.builder()
                                        .latitude(19.0760)
                                        .longitude(72.8777)
                                        .build();

                        // Act
                        double bearing = geoLocationService.calculateBearing(bangalore, mumbai);

                        // Assert - Mumbai is northwest of Bangalore, so bearing should be around
                        // 320-330 degrees
                        assertThat(bearing).isBetween(320.0, 340.0);
                }

                @Test
                @DisplayName("Should return bearing in 0-360 range")
                void shouldReturnBearingInValidRange() {
                        // Arrange
                        GeoLocation from = GeoLocation.builder()
                                        .latitude(0.0)
                                        .longitude(0.0)
                                        .build();

                        GeoLocation to = GeoLocation.builder()
                                        .latitude(10.0)
                                        .longitude(-10.0)
                                        .build();

                        // Act
                        double bearing = geoLocationService.calculateBearing(from, to);

                        // Assert
                        assertThat(bearing).isBetween(0.0, 360.0);
                }
        }
}
