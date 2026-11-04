package com.trakto.traktoroute.routes.application.queries;

import com.trakto.traktoroute.routes.domain.model.valueobjects.GeoPoint;

import java.util.Objects;

public record ComputeRouteQuery(
        GeoPoint origin,
        GeoPoint destination
) {

    public ComputeRouteQuery {
        Objects.requireNonNull(origin, "Origin is required");
        Objects.requireNonNull(destination, "Destination is required");
    }
}