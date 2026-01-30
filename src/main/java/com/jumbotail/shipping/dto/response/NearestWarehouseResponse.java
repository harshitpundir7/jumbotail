package com.jumbotail.shipping.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for the nearest warehouse API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Nearest warehouse response")
public class NearestWarehouseResponse {

    @Schema(description = "Unique warehouse identifier", example = "1")
    private Long warehouseId;

    @Schema(description = "Warehouse code", example = "BLR_WH_01")
    private String warehouseCode;

    @Schema(description = "Warehouse name", example = "Bangalore Central Warehouse")
    private String warehouseName;

    @Schema(description = "Warehouse location coordinates")
    private LocationResponse warehouseLocation;

    @Schema(description = "Distance from seller in kilometers", example = "45.5")
    private Double distanceKm;
}
