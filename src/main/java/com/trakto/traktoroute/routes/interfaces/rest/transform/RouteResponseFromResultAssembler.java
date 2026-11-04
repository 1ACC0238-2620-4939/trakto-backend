package com.trakto.traktoroute.routes.interfaces.rest.transform;

import com.trakto.traktoroute.routes.domain.model.valueobjects.RouteResult;
import com.trakto.traktoroute.routes.interfaces.rest.dto.responses.RouteResponse;

public final class RouteResponseFromResultAssembler {

    private RouteResponseFromResultAssembler() {
    }

    public static RouteResponse toResponse(RouteResult route) {
        return new RouteResponse(
                route.distanceMeters(),
                route.distanceKm(),
                route.durationSeconds(),
                route.durationMinutes(),
                route.encodedPolyline(),
                route.calculatedAt()
        );
    }
}