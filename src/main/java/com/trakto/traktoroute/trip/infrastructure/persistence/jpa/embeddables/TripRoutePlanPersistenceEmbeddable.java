package com.trakto.traktoroute.trip.infrastructure.persistence.jpa.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class TripRoutePlanPersistenceEmbeddable {

    @Column(name = "route_distance_km", nullable = false)
    private BigDecimal distanceKm;

    @Column(name = "route_duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "route_reference", nullable = false, columnDefinition = "LONGTEXT")
    private String routeReference;

    @Column(name = "route_calculated_at", nullable = false)
    private Instant calculatedAt;

    public TripRoutePlanPersistenceEmbeddable(BigDecimal distanceKm,
                                              Integer durationMinutes,
                                              String routeReference,
                                              Instant calculatedAt) {
        this.distanceKm = distanceKm;
        this.durationMinutes = durationMinutes;
        this.routeReference = routeReference;
        this.calculatedAt = calculatedAt;
    }
}