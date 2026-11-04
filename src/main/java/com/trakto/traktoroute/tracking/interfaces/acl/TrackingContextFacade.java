package com.trakto.traktoroute.tracking.interfaces.acl;

import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.application.result.Result;

import java.util.UUID;

public interface TrackingContextFacade {

    Result<Void, ApplicationError> startTracking(UUID tripId);

    Result<Void, ApplicationError> finishTracking(UUID tripId);
}