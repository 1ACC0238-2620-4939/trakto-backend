package com.trakto.traktoroute.routes.application.acl;

import com.trakto.traktoroute.routes.application.queries.ComputeRouteQuery;
import com.trakto.traktoroute.routes.application.services.RouteQueryService;
import com.trakto.traktoroute.routes.domain.model.valueobjects.GeoPoint;
import com.trakto.traktoroute.routes.interfaces.acl.RouteSummary;
import com.trakto.traktoroute.routes.interfaces.acl.RoutesContextFacade;
import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class RoutesContextFacadeImpl implements RoutesContextFacade {

    private final RouteQueryService routeQueryService;

    public RoutesContextFacadeImpl(RouteQueryService routeQueryService) {
        this.routeQueryService = routeQueryService;
    }

    @Override
    public Result<RouteSummary, ApplicationError> computeRoute(
            double originLatitude,
            double originLongitude,
            double destinationLatitude,
            double destinationLongitude) {

        ComputeRouteQuery query;

        try {
            query = new ComputeRouteQuery(
                    new GeoPoint(originLatitude, originLongitude),
                    new GeoPoint(destinationLatitude, destinationLongitude)
            );
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError
                    .validationError("Route", e.getMessage())
            );
        }

        return routeQueryService.handle(query)
                .map(route -> new RouteSummary(
                        route.distanceKm(),
                        route.durationMinutes(),
                        route.encodedPolyline(),
                        route.calculatedAt()
                ));
    }
}