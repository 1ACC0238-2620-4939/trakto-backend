package com.trakto.traktoroute.routes.interfaces.acl;

import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.application.result.Result;

public interface RoutesContextFacade {

    Result<RouteSummary, ApplicationError> computeRoute(
            double originLatitude,
            double originLongitude,
            double destinationLatitude,
            double destinationLongitude
    );
}