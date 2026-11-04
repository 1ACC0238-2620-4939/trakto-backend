package com.trakto.traktoroute.routes.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

public record RouteResult(
        long distanceMeters,
        long durationSeconds,
        String encodedPolyline,
        Instant calculatedAt
) {
    public RouteResult {
        if (distanceMeters <= 0 || durationSeconds <= 0) {
            throw new IllegalArgumentException("Invalid route");
        }
        if (encodedPolyline == null || encodedPolyline.isBlank()) {
            throw new IllegalArgumentException("Route polyline cannot be null or blank");
        }
        if (calculatedAt == null) {
            throw new IllegalArgumentException("Calculated at cannot be null");
        }
    }

    public BigDecimal distanceKm() {
        return BigDecimal.valueOf(distanceMeters)
                .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);
    }

    public int durationMinutes() {
        return (int) Math.ceil(durationSeconds / 60.0);
    }
}