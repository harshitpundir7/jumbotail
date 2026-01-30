package com.jumbotail.shipping.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for geographical location.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Geographical location coordinates")
public class LocationResponse {

    @Schema(description = "Latitude coordinate", example = "12.99999")
    private Double lat;

    @Schema(description = "Longitude coordinate", example = "37.923273")
    private Double lng;
}
