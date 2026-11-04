package com.trakto.traktoroute.tracking.application.services;

import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.application.result.Result;
import com.trakto.traktoroute.tracking.application.commands.ChangeStopReasonCommand;
import com.trakto.traktoroute.tracking.application.commands.CreateTrackingCommand;
import com.trakto.traktoroute.tracking.application.commands.FinishTrackingCommand;
import com.trakto.traktoroute.tracking.application.commands.RegisterPositionCommand;
import com.trakto.traktoroute.tracking.domain.model.aggregates.Tracking;
import com.trakto.traktoroute.tracking.domain.repositories.TrackingRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TrackingCommandService {

    private final TrackingRepository trackingRepository;

    public TrackingCommandService(TrackingRepository trackingRepository) {
        this.trackingRepository = trackingRepository;
    }

    @Transactional
    public Result<Tracking, ApplicationError> handle(CreateTrackingCommand command) {
        if (trackingRepository.existsByTripReferenceId(command.tripReferenceId())) {
            return Result.failure(
                    ApplicationError.conflict(
                            "Tracking",
                            "A tracking already exists for this trip"
                    )
            );
        }

        Tracking tracking = Tracking.create(command.tripReferenceId());

        Tracking savedTracking = trackingRepository.save(tracking);

        return Result.success(savedTracking);
    }

    @Transactional
    public Result<Tracking, ApplicationError> handle(
            RegisterPositionCommand command
    ) {
        var trackingOptional = trackingRepository.findById(
                command.trackingId()
        );

        if (trackingOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Tracking",
                            command.trackingId().value().toString()
                    )
            );
        }

        Tracking tracking = trackingOptional.get();

        try {
            tracking.registerPosition(command.positionReport());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.failure(
                    ApplicationError.businessRuleViolation(
                            "Tracking position registration",
                            e.getMessage()
                    )
            );
        }

        Tracking savedTracking = trackingRepository.save(tracking);

        return Result.success(savedTracking);
    }

    @Transactional
    public Result<Tracking, ApplicationError> handle(
            ChangeStopReasonCommand command
    ) {
        var trackingOptional = trackingRepository.findById(
                command.trackingId()
        );

        if (trackingOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Tracking",
                            command.trackingId().value().toString()
                    )
            );
        }

        Tracking tracking = trackingOptional.get();

        var stopOptional = tracking.getStops().stream()
                .filter(stop -> stop.getStopId().equals(command.stopId()))
                .findFirst();

        if (stopOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound(
                            "TrackingStop",
                            command.stopId().value().toString()
                    )
            );
        }

        if (stopOptional.get().getReason() == command.reason()) {
            return Result.success(tracking);
        }

        try {
            tracking.changeStopReason(
                    command.stopId(),
                    command.reason()
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.failure(
                    ApplicationError.businessRuleViolation(
                            "Tracking stop reason change",
                            e.getMessage()
                    )
            );
        }

        Tracking savedTracking = trackingRepository.save(tracking);

        return Result.success(savedTracking);
    }

    @Transactional
    public Result<Tracking, ApplicationError> handle(
            FinishTrackingCommand command
    ) {
        var trackingOptional = trackingRepository.findById(
                command.trackingId()
        );

        if (trackingOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Tracking",
                            command.trackingId().value().toString()
                    )
            );
        }

        Tracking tracking = trackingOptional.get();

        try {
            tracking.finish(Instant.now());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.failure(
                    ApplicationError.businessRuleViolation(
                            "Tracking finish",
                            e.getMessage()
                    )
            );
        }

        Tracking savedTracking = trackingRepository.save(tracking);

        return Result.success(savedTracking);
    }
}