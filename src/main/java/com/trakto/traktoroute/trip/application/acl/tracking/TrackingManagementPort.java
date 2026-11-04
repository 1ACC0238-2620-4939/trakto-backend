package com.trakto.traktoroute.trip.application.acl.tracking;

import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.application.result.Result;

import java.util.UUID;

public interface TrackingManagementPort {

    Result<Void, ApplicationError> startTracking(UUID tripId);

    Result<Void, ApplicationError> finishTracking(UUID tripId);
}