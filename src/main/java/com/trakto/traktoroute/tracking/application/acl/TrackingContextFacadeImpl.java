package com.trakto.traktoroute.tracking.application.acl;

import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.application.result.Result;
import com.trakto.traktoroute.tracking.application.commands.CreateTrackingCommand;
import com.trakto.traktoroute.tracking.application.commands.FinishTrackingCommand;
import com.trakto.traktoroute.tracking.application.queries.GetTrackingByTripIdQuery;
import com.trakto.traktoroute.tracking.application.services.TrackingCommandService;
import com.trakto.traktoroute.tracking.application.services.TrackingQueryService;
import com.trakto.traktoroute.tracking.domain.model.aggregates.Tracking;
import com.trakto.traktoroute.tracking.domain.model.valueobjects.TripReferenceId;
import com.trakto.traktoroute.tracking.interfaces.acl.TrackingContextFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TrackingContextFacadeImpl implements TrackingContextFacade {

    private final TrackingCommandService trackingCommandService;
    private final TrackingQueryService trackingQueryService;

    public TrackingContextFacadeImpl(TrackingCommandService trackingCommandService,
                                     TrackingQueryService trackingQueryService
    ) {
        this.trackingCommandService = trackingCommandService;
        this.trackingQueryService = trackingQueryService;
    }

    @Override
    public Result<Void, ApplicationError> startTracking(UUID tripId) {

        var command = new CreateTrackingCommand(new TripReferenceId(tripId));

        return trackingCommandService.handle(command)
                .map(tracking -> (Void) null);
    }

    @Override
    public Result<Void, ApplicationError> finishTracking(UUID tripId) {

        Tracking tracking;

        try {
            var query = new GetTrackingByTripIdQuery(new TripReferenceId(tripId));

            tracking = trackingQueryService.handle(query);

        } catch (IllegalArgumentException e) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Tracking",
                            tripId.toString()
                    )
            );
        }

        var command = new FinishTrackingCommand(
                tracking.getTrackingId()
        );

        return trackingCommandService.handle(command)
                .map(savedTracking -> (Void) null);
    }
}