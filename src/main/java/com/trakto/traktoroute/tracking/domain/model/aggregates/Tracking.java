package com.trakto.traktoroute.tracking.domain.model.aggregates;

import com.trakto.traktoroute.shared.domain.models.aggregates.AbstractDomainAggregateRoot;
import com.trakto.traktoroute.tracking.domain.model.entities.TrackingStop;
import com.trakto.traktoroute.tracking.domain.model.enums.StopReason;
import com.trakto.traktoroute.tracking.domain.model.enums.TrackingStatus;
import com.trakto.traktoroute.tracking.domain.model.events.*;
import com.trakto.traktoroute.tracking.domain.model.valueobjects.*;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class Tracking extends AbstractDomainAggregateRoot<Tracking> {

    private static final Duration MINIMUM_STOP_DURATION =
            Duration.ofMinutes(10);

    private static final double STOP_RADIUS_METERS = 20.0;
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private final TrackingId trackingId;
    private final TripReferenceId tripReferenceId;

    private TrackingStatus status;
    private PositionReport lastPositionReport;

    private GeoLocation stationaryLocation;
    private Instant stationarySince;

    private final List<TrackingStop> stops;

    private Tracking(
            TrackingId trackingId,
            TripReferenceId tripReferenceId,
            TrackingStatus status,
            PositionReport lastPositionReport,
            GeoLocation stationaryLocation,
            Instant stationarySince,
            List<TrackingStop> stops
    ) {
        this.trackingId = Objects.requireNonNull(
                trackingId, "Tracking id cannot be null"
        );
        this.tripReferenceId = Objects.requireNonNull(
                tripReferenceId, "Trip reference id cannot be null"
        );
        this.status = Objects.requireNonNull(
                status, "Tracking status cannot be null"
        );

        this.lastPositionReport = lastPositionReport;
        this.stationaryLocation = stationaryLocation;
        this.stationarySince = stationarySince;

        Objects.requireNonNull(stops, "Stops cannot be null");

        if (stops.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "Stops cannot contain null"
            );
        }

        this.stops = new ArrayList<>();

        for (TrackingStop stop : stops) {
            this.stops.add(copyStop(stop));
        }

        validateState();
    }

    public static Tracking create(TripReferenceId tripReferenceId) {
        var tracking = new Tracking(
                TrackingId.generate(),
                tripReferenceId,
                TrackingStatus.ACTIVE,
                null,
                null,
                null,
                List.of()
        );

        tracking.registerDomainEvent(
                new TrackingStartedEvent(
                        tracking.trackingId,
                        tracking.tripReferenceId,
                        Instant.now()
                )
        );

        return tracking;
    }

    public static Tracking reconstitute(
            TrackingId trackingId,
            TripReferenceId tripReferenceId,
            TrackingStatus status,
            PositionReport lastPositionReport,
            GeoLocation stationaryLocation,
            Instant stationarySince,
            List<TrackingStop> stops
    ) {
        return new Tracking(
                trackingId,
                tripReferenceId,
                status,
                lastPositionReport,
                stationaryLocation,
                stationarySince,
                stops
        );
    }

    public void registerPosition(PositionReport positionReport) {
        ensureActive();

        Objects.requireNonNull(
                positionReport, "Position report cannot be null"
        );

        if (lastPositionReport != null
                && !positionReport.recordedAt()
                .isAfter(lastPositionReport.recordedAt())) {
            throw new IllegalArgumentException(
                    "Position report must be newer than the previous report"
            );
        }

        GeoLocation location = positionReport.location();
        Instant recordedAt = positionReport.recordedAt();

        if (stationaryLocation == null) {
            beginStationaryPeriod(location, recordedAt);
        } else
            if (distanceInMeters(stationaryLocation, location) > STOP_RADIUS_METERS) {

            TrackingStop openStop = findOpenStop();

            if (openStop != null) {
                finishStop(openStop, recordedAt);
            }

            beginStationaryPeriod(location, recordedAt);
        } else {
            Duration stationaryDuration = Duration.between(stationarySince, recordedAt);

            if (stationaryDuration.compareTo(MINIMUM_STOP_DURATION) >= 0
                    && findOpenStop() == null) {

                var stop = new TrackingStop(
                        stationaryLocation,
                        stationarySince
                );

                stops.add(stop);

                registerDomainEvent(
                        new TrackingStopDetectedEvent(
                                trackingId,
                                tripReferenceId,
                                stop.getStopId(),
                                stop.getLocation(),
                                stop.getStartedAt(),
                                recordedAt
                        )
                );
            }
        }

        lastPositionReport = positionReport;
    }

    public void changeStopReason(StopId stopId, StopReason reason) {
        Objects.requireNonNull(stopId, "Stop id cannot be null");
        Objects.requireNonNull(reason, "Stop reason cannot be null");

        TrackingStop stop = stops.stream()
                .filter(item -> item.getStopId().equals(stopId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Stop does not belong to this tracking"
                ));

        StopReason previousReason = stop.getReason();

        if (previousReason == reason) {
            return;
        }

        stop.changeReason(reason);

        registerDomainEvent(
                new TrackingStopReasonChangedEvent(
                        trackingId,
                        tripReferenceId,
                        stopId,
                        previousReason,
                        reason,
                        Instant.now()
                )
        );
    }

    public void finish(Instant finishedAt) {
        Objects.requireNonNull(
                finishedAt, "Finish time cannot be null"
        );

        ensureActive();

        if (lastPositionReport != null
                && finishedAt.isBefore(lastPositionReport.recordedAt())) {
            throw new IllegalArgumentException(
                    "Finish time cannot precede the last position report"
            );
        }

        TrackingStop openStop = findOpenStop();

        if (openStop != null) {
            finishStop(openStop, finishedAt);
        }

        status = TrackingStatus.FINISHED;
        stationaryLocation = null;
        stationarySince = null;

        registerDomainEvent(
                new TrackingFinishedEvent(
                        trackingId,
                        tripReferenceId,
                        finishedAt
                )
        );
    }

    private void finishStop(TrackingStop stop, Instant endedAt) {
        stop.finish(endedAt);

        registerDomainEvent(
                new TrackingStopFinishedEvent(
                        trackingId,
                        tripReferenceId,
                        stop.getStopId(),
                        endedAt
                )
        );
    }

    private void beginStationaryPeriod(
            GeoLocation location,
            Instant recordedAt
    ) {
        stationaryLocation = location;
        stationarySince = recordedAt;
    }

    private TrackingStop findOpenStop() {
        return stops.stream()
                .filter(TrackingStop::isOpen)
                .findFirst()
                .orElse(null);
    }

    private void ensureActive() {
        if (status != TrackingStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Tracking has already finished"
            );
        }
    }

    private void validateState() {
        if ((stationaryLocation == null) != (stationarySince == null)) {
            throw new IllegalArgumentException(
                    "Stationary location and time must be provided together"
            );
        }

        long distinctStopIds = stops.stream()
                .map(TrackingStop::getStopId)
                .distinct()
                .count();

        if (distinctStopIds != stops.size()) {
            throw new IllegalArgumentException(
                    "Stop ids must be unique"
            );
        }

        long openStops = stops.stream()
                .filter(TrackingStop::isOpen)
                .count();

        if (openStops > 1) {
            throw new IllegalArgumentException(
                    "Tracking cannot have multiple open stops"
            );
        }

        if (status == TrackingStatus.FINISHED
                && (openStops > 0 || stationarySince != null)) {
            throw new IllegalArgumentException(
                    "Finished tracking cannot have an open stationary period"
            );
        }

        if (status == TrackingStatus.ACTIVE
                && (lastPositionReport == null)
                != (stationarySince == null)) {
            throw new IllegalArgumentException(
                    "Active tracking position and stationary period are inconsistent"
            );
        }

        if (stationarySince != null) {
            if (lastPositionReport == null) {
                throw new IllegalArgumentException(
                        "Stationary period requires a position report"
                );
            }

            if (stationarySince.isAfter(lastPositionReport.recordedAt())) {
                throw new IllegalArgumentException(
                        "Stationary time cannot follow the last position report"
                );
            }
        }

        if (openStops == 1) {
            TrackingStop openStop = findOpenStop();

            if (stationarySince == null
                    || !openStop.getStartedAt().equals(stationarySince)
                    || !openStop.getLocation().equals(stationaryLocation)) {
                throw new IllegalArgumentException(
                        "Open stop must match the stationary period"
                );
            }
        }
    }

    private static double distanceInMeters(
            GeoLocation origin,
            GeoLocation destination
    ) {
        double originLatitude = Math.toRadians(origin.latitude());
        double destinationLatitude = Math.toRadians(destination.latitude());

        double latitudeDifference = destinationLatitude - originLatitude;

        double longitudeDifference =
                Math.toRadians(destination.longitude() - origin.longitude());

        // Haversine
        double a = Math.pow(Math.sin(latitudeDifference / 2), 2)
                        + Math.cos(originLatitude)
                        * Math.cos(destinationLatitude)
                        * Math.pow(Math.sin(longitudeDifference / 2), 2);

        a = Math.clamp(a, 0.0, 1.0);

        return EARTH_RADIUS_METERS
                * 2
                * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private static TrackingStop copyStop(TrackingStop stop) {
        return new TrackingStop(
                stop.getStopId(),
                stop.getLocation(),
                stop.getStartedAt(),
                stop.getEndedAt(),
                stop.getReason()
        );
    }

    public List<TrackingStop> getStops() {
        return stops.stream()
                .map(Tracking::copyStop)
                .toList();
    }
}