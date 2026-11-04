package com.trakto.traktoroute.routes.domain.port.gateway;

import com.trakto.traktoroute.routes.domain.model.valueobjects.GeoPoint;
import com.trakto.traktoroute.routes.domain.model.valueobjects.RouteResult;

import java.util.List;
import java.util.Optional;

public interface RoutingGateway {
    /** Optional.empty() = Google respondió pero no hay ruta.
     *  Lanza RoutingUnavailableException si el proveedor falla. */
    Optional<RouteResult> computeRoute(GeoPoint origin, GeoPoint destination);
}