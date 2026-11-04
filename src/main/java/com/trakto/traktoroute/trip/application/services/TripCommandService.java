package com.trakto.traktoroute.trip.application.services;

import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.application.result.Result;
import com.trakto.traktoroute.trip.application.acl.fleet.FleetAvailabilityPort;
import com.trakto.traktoroute.trip.application.acl.tracking.TrackingManagementPort;
import com.trakto.traktoroute.trip.application.commands.CancelTripCommand;
import com.trakto.traktoroute.trip.application.commands.CompleteTripCommand;
import com.trakto.traktoroute.trip.application.commands.CreateTripCommand;
import com.trakto.traktoroute.trip.application.commands.StartTripCommand;
import com.trakto.traktoroute.trip.domain.model.aggregates.Trip;
import com.trakto.traktoroute.trip.domain.model.enums.TripStatus;
import com.trakto.traktoroute.trip.domain.model.valueobjects.TripId;
import com.trakto.traktoroute.trip.domain.repositories.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TripCommandService {

    private final TripRepository tripRepository;
    private final FleetAvailabilityPort fleetAvailabilityPort;
    private final TrackingManagementPort trackingManagementPort;

    public TripCommandService(
            TripRepository tripRepository,
            FleetAvailabilityPort fleetAvailabilityPort,
            TrackingManagementPort trackingManagementPort
    ) {
        this.tripRepository = tripRepository;
        this.fleetAvailabilityPort = fleetAvailabilityPort;
        this.trackingManagementPort = trackingManagementPort;
    }

    @Transactional
    public Result<TripId, ApplicationError> handle(
            CreateTripCommand command
    ) {

        if (!fleetAvailabilityPort.canAssignDriver(
                command.driverId().value())) {

            return Result.failure(
                    ApplicationError.businessRuleViolation(
                            "Trip",
                            "Driver does not exist or is inactive"
                    )
            );
        }

        if (!fleetAvailabilityPort.canAssignVehicle(
                command.vehicleId().value())) {

            return Result.failure(
                    ApplicationError.businessRuleViolation(
                            "Trip",
                            "Vehicle does not exist or is inactive"
                    )
            );
        }

        Trip trip;

        try {
            trip = Trip.create(
                    command.driverId(),
                    command.vehicleId(),
                    command.origin(),
                    command.destination(),
                    command.schedule(),
                    command.routePlan()
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.failure(
                    ApplicationError.validationError(
                            "Trip", e.getMessage()
                    )
            );
        }

        Trip savedTrip = tripRepository.save(trip);

        return Result.success(savedTrip.getId());
    }

    @Transactional
    public Result<Void, ApplicationError> handle(
            StartTripCommand command
    ) {
        var tripOptional = tripRepository.findById(command.tripId());

        if (tripOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound("Trip", "Trip not found")
            );
        }

        Trip trip = tripOptional.get();

        try {
            trip.start(command.startedAt());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.failure(
                    ApplicationError.validationError(
                            "Trip", e.getMessage()
                    )
            );
        }

        var trackingResult = trackingManagementPort.startTracking(
                trip.getId().value()
        );

        if (trackingResult.isFailure()) {
            return trackingResult;
        }

        tripRepository.save(trip);

        return Result.success(null);
    }

    @Transactional
    public Result<Void, ApplicationError> handle(
            CompleteTripCommand command
    ) {
        var tripOptional = tripRepository.findById(command.tripId());

        if (tripOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound("Trip", "Trip not found")
            );
        }

        Trip trip = tripOptional.get();

        try {
            trip.complete(command.completedAt());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.failure(
                    ApplicationError.validationError(
                            "Trip", e.getMessage()
                    )
            );
        }

        var trackingResult = trackingManagementPort.finishTracking(
                trip.getId().value()
        );

        if (trackingResult.isFailure()) {
            return trackingResult;
        }

        tripRepository.save(trip);

        return Result.success(null);
    }

    @Transactional
    public Result<Void, ApplicationError> handle(
            CancelTripCommand command
    ) {
        var tripOptional = tripRepository.findById(command.tripId());

        if (tripOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound("Trip", "Trip not found")
            );
        }

        Trip trip = tripOptional.get();

        boolean wasInProgress =
                trip.getStatus() == TripStatus.IN_PROGRESS;

        try {
            trip.cancel(command.cancelledAt());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.failure(
                    ApplicationError.validationError(
                            "Trip", e.getMessage()
                    )
            );
        }

        if (wasInProgress) {

            var trackingResult =
                    trackingManagementPort.finishTracking(
                            trip.getId().value()
                    );

            if (trackingResult.isFailure()) {
                return trackingResult;
            }
        }

        tripRepository.save(trip);

        return Result.success(null);
    }
}