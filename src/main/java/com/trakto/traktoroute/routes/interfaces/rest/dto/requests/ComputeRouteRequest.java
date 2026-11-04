package com.trakto.traktoroute.routes.interfaces.rest.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ComputeRouteRequest",
        description = "Request to compute a route between two points")
public record ComputeRouteRequest(

        @NotNull(message = "Origin latitude is required")
        @DecimalMin(value = "-90.0", message = "Origin latitude must be at least -90")
        @DecimalMax(value = "90.0", message = "Origin latitude must be at most 90")
        @Schema(description = "Origin latitude", example = "-12.043")
        Double originLatitude,

        @NotNull(message = "Origin longitude is required")
        @DecimalMin(value = "-180.0", message = "Origin longitude must be at least -180")
        @DecimalMax(value = "180.0", message = "Origin longitude must be at most 180")
        @Schema(description = "Origin longitude", example = "-76.971")
        Double originLongitude,

        @NotNull(message = "Destination latitude is required")
        @DecimalMin(value = "-90.0", message = "Destination latitude must be at least -90")
        @DecimalMax(value = "90.0", message = "Destination latitude must be at most 90")
        @Schema(description = "Destination latitude", example = "-12.121")
        Double destinationLatitude,

        @NotNull(message = "Destination longitude is required")
        @DecimalMin(value = "-180.0", message = "Destination longitude must be at least -180")
        @DecimalMax(value = "180.0", message = "Destination longitude must be at most 180")
        @Schema(description = "Destination longitude", example = "-77.029")
        Double destinationLongitude
) {
}