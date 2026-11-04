package com.trakto.traktoroute.routes.infrastructure.google.adapters;

import com.google.api.gax.rpc.ApiException;
import com.google.maps.routing.v2.RoutesClient;
import com.trakto.traktoroute.routes.domain.exceptions.RoutingUnavailableException;
import com.trakto.traktoroute.routes.domain.model.valueobjects.GeoPoint;
import com.trakto.traktoroute.routes.domain.model.valueobjects.RouteResult;
import com.trakto.traktoroute.routes.domain.port.gateway.RoutingGateway;
import com.trakto.traktoroute.routes.infrastructure.google.assembler.GoogleRouteAssembler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class GoogleRoutingGatewayAdapter implements RoutingGateway {

    private final RoutesClient routesClient;

    public GoogleRoutingGatewayAdapter(RoutesClient routesClient) {
        this.routesClient = routesClient;
    }

    @Override
    public Optional<RouteResult> computeRoute(GeoPoint origin, GeoPoint destination) {
        var request = GoogleRouteAssembler.toGoogleFromDomain(origin, destination);

        try {
            var response = routesClient.computeRoutes(request);

            if (response.getRoutesCount() == 0) {
                return Optional.empty();
            }

            return Optional.of(
                    GoogleRouteAssembler.toDomainFromGoogle(
                            response.getRoutes(0),
                            Instant.now()
                    )
            );
        } catch (ApiException e) {
            throw new RoutingUnavailableException(
                    "Google Routes API call failed", e);
        }
    }
}