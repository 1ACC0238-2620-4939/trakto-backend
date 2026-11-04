package com.trakto.traktoroute.routes.interfaces.rest.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(name = "RouteResponse",
        description = "Computed route between two points")
public record RouteResponse(

        @Schema(description = "Route distance in meters", example = "21500")
        long distanceMeters,

        @Schema(description = "Route distance in kilometers", example = "21.50")
        BigDecimal distanceKm,

        @Schema(description = "Estimated duration in seconds", example = "3300")
        long durationSeconds,

        @Schema(description = "Estimated duration in minutes", example = "55")
        int durationMinutes,

        @Schema(description = "Google encoded polyline to draw the route on a map",
                example = "o~fnKnzdvLiBsA...")
        String encodedPolyline,

        @Schema(description = "Date and time when the route was calculated",
                example = "2026-11-04T18:00:00Z")
        Instant calculatedAt
) {
}