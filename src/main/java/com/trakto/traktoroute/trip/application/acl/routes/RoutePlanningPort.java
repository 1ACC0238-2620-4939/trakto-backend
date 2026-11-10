package com.trakto.traktoroute.trip.application.acl.routes;

import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.application.result.Result;
import com.trakto.traktoroute.trip.domain.model.valueobjects.TripLocation;
import com.trakto.traktoroute.trip.domain.model.valueobjects.TripRoutePlan;

public interface RoutePlanningPort {

    Result<TripRoutePlan, ApplicationError> planRoute(
            TripLocation origin,
            TripLocation destination
    );
}