package com.trakto.traktoroute.routes.interfaces.rest.controllers;

import com.trakto.traktoroute.routes.application.queries.ComputeRouteQuery;
import com.trakto.traktoroute.routes.application.services.RouteQueryService;
import com.trakto.traktoroute.routes.interfaces.rest.dto.requests.ComputeRouteRequest;
import com.trakto.traktoroute.routes.interfaces.rest.transform.ComputeRouteQueryFromRequestAssembler;
import com.trakto.traktoroute.routes.interfaces.rest.transform.RouteResponseFromResultAssembler;
import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.trakto.traktoroute.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/routes")
@Tag(name = "Route - Query",
        description = "Route calculation")
public class RouteQueryController {

    private final RouteQueryService routeQueryService;

    public RouteQueryController(RouteQueryService routeQueryService) {
        this.routeQueryService = routeQueryService;
    }

    @GetMapping
    @Operation(
            summary = "Compute a route",
            description = "Computes the driving route between an origin and a destination, "
                    + "including the encoded polyline to draw it"
    )
    public ResponseEntity<?> computeRoute(
            @Valid @ParameterObject ComputeRouteRequest request
    ) {
        ComputeRouteQuery query;

        try {
            query = ComputeRouteQueryFromRequestAssembler.toQuery(request);
        } catch (IllegalArgumentException | NullPointerException e) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    ApplicationError.validationError("Route", e.getMessage())
            );
        }

        var result = routeQueryService.handle(query);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                RouteResponseFromResultAssembler::toResponse,
                HttpStatus.OK
        );
    }
}