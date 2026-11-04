package com.trakto.traktoroute.routes.interfaces.rest.transform;

import com.trakto.traktoroute.routes.application.queries.ComputeRouteQuery;
import com.trakto.traktoroute.routes.domain.model.valueobjects.GeoPoint;
import com.trakto.traktoroute.routes.interfaces.rest.dto.requests.ComputeRouteRequest;

public final class ComputeRouteQueryFromRequestAssembler {

    private ComputeRouteQueryFromRequestAssembler() {
    }

    public static ComputeRouteQuery toQuery(ComputeRouteRequest request) {
        return new ComputeRouteQuery(
                new GeoPoint(
                        request.originLatitude(),
                        request.originLongitude()
                ),
                new GeoPoint(
                        request.destinationLatitude(),
                        request.destinationLongitude()
                )
        );
    }
}