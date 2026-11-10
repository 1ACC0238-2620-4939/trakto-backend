package com.trakto.traktoroute.routes.interfaces.acl;

import java.math.BigDecimal;
import java.time.Instant;

public record RouteSummary(
        BigDecimal distanceKm,
        int durationMinutes,
        String encodedPolyline,
        Instant calculatedAt
) {
}