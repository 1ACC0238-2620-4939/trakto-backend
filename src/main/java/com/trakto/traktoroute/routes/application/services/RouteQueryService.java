package com.trakto.traktoroute.routes.application.services;

import com.trakto.traktoroute.routes.application.queries.ComputeRouteQuery;
import com.trakto.traktoroute.routes.domain.exceptions.RoutingUnavailableException;
import com.trakto.traktoroute.routes.domain.model.valueobjects.RouteResult;
import com.trakto.traktoroute.routes.domain.port.gateway.RoutingGateway;
import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class RouteQueryService {

    private final RoutingGateway routingGateway;

    public RouteQueryService(RoutingGateway routingGateway) {
        this.routingGateway = routingGateway;
    }

    public Result<RouteResult, ApplicationError> handle(ComputeRouteQuery query) {

        if (query.origin().equals(query.destination())) {
            return Result.failure(
                    ApplicationError.validationError(
                            "Route",
                            "Origin and destination must be different"
                    )
            );
        }

        try {
            var routeOptional = routingGateway.computeRoute(
                    query.origin(),
                    query.destination()
            );


            return routeOptional.<Result<RouteResult, ApplicationError>>map(Result::success).orElseGet(() -> Result.failure(
                    ApplicationError.notFound(
                            "Route",
                            "no route between the given origin and destination"
                    )
            ));
            /*
            if (routeOptional.isEmpty()) {
                return Result.failure(
                        ApplicationError.notFound(
                                "Route",
                                "no route between the given origin and destination"
                        )
                );
            }
             */

        } catch (RoutingUnavailableException e) {
            return Result.failure(
                    ApplicationError.unexpected(
                            "Route calculation",
                            e.getMessage()
                    )
            );
        }
    }
}