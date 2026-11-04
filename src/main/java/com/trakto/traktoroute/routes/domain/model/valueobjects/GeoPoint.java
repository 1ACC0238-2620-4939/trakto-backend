package com.trakto.traktoroute.routes.domain.model.valueobjects;

public record GeoPoint(
        double latitude,
        double longitude
) {
    public GeoPoint {
        if (!Double.isFinite(latitude)
                || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude");
        }

        if (!Double.isFinite(longitude)
                || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude");
        }
    }
}