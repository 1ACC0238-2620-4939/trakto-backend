package com.trakto.traktoroute.trip.application.acl.routes;

import com.trakto.traktoroute.routes.interfaces.acl.RoutesContextFacade;
import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.application.result.Result;
import com.trakto.traktoroute.trip.domain.model.valueobjects.TripLocation;
import com.trakto.traktoroute.trip.domain.model.valueobjects.TripRoutePlan;
import org.springframework.stereotype.Service;

@Service
public class RoutesContextAdapter implements RoutePlanningPort {

    private final RoutesContextFacade routesContextFacade;

    public RoutesContextAdapter(RoutesContextFacade routesContextFacade) {
        this.routesContextFacade = routesContextFacade;
    }

    @Override
    public Result<TripRoutePlan, ApplicationError> planRoute(
            TripLocation origin,
            TripLocation destination
    ) {
        return routesContextFacade.computeRoute(
                        origin.latitude().doubleValue(),
                        origin.longitude().doubleValue(),
                        destination.latitude().doubleValue(),
                        destination.longitude().doubleValue()
                )
                .map(summary -> new TripRoutePlan(
                        summary.distanceKm(),
                        summary.durationMinutes(),
                        summary.encodedPolyline(),
                        summary.calculatedAt()
                ));
    }
}