package com.trakto.traktoroute.trip.application.acl.tracking;

import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.application.result.Result;
import com.trakto.traktoroute.tracking.interfaces.acl.TrackingContextFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TrackingContextAdapter implements TrackingManagementPort {

    private final TrackingContextFacade trackingContextFacade;

    public TrackingContextAdapter(TrackingContextFacade trackingContextFacade) {
        this.trackingContextFacade = trackingContextFacade;
    }

    @Override
    public Result<Void, ApplicationError> startTracking(UUID tripId) {
        return trackingContextFacade.startTracking(tripId);
    }

    @Override
    public Result<Void, ApplicationError> finishTracking(UUID tripId) {
        return trackingContextFacade.finishTracking(tripId);
    }
}