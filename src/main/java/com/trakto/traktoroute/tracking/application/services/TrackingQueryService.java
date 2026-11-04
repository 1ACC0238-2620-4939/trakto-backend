package com.trakto.traktoroute.tracking.application.services;

import com.trakto.traktoroute.tracking.application.queries.GetLastPositionByTripIdQuery;
import com.trakto.traktoroute.tracking.application.queries.GetStopByTripIdAndStopIdQuery;
import com.trakto.traktoroute.tracking.application.queries.GetStopsByTripIdQuery;
import com.trakto.traktoroute.tracking.application.queries.GetTrackingByIdQuery;
import com.trakto.traktoroute.tracking.application.queries.GetTrackingByTripIdQuery;
import com.trakto.traktoroute.tracking.domain.model.aggregates.Tracking;
import com.trakto.traktoroute.tracking.domain.model.entities.TrackingStop;
import com.trakto.traktoroute.tracking.domain.model.valueobjects.PositionReport;
import com.trakto.traktoroute.tracking.domain.model.valueobjects.TripReferenceId;
import com.trakto.traktoroute.tracking.domain.repositories.TrackingRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TrackingQueryService {

    private final TrackingRepository trackingRepository;

    public TrackingQueryService(TrackingRepository trackingRepository) {
        this.trackingRepository = trackingRepository;
    }

    public Tracking handle(GetTrackingByIdQuery query) {
        return trackingRepository.findById(query.trackingId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Tracking not found")
                );
    }

    public Tracking handle(GetTrackingByTripIdQuery query) {
        return findTrackingByTripOrThrow(
                query.tripReferenceId()
        );
    }

    public Optional<PositionReport> handle(
            GetLastPositionByTripIdQuery query
    ) {
        Tracking tracking = findTrackingByTripOrThrow(
                query.tripReferenceId()
        );

        return Optional.ofNullable(
                tracking.getLastPositionReport()
        );
    }

    public List<TrackingStop> handle(
            GetStopsByTripIdQuery query
    ) {
        Tracking tracking = findTrackingByTripOrThrow(
                query.tripReferenceId()
        );

        return tracking.getStops();
    }

    public TrackingStop handle(
            GetStopByTripIdAndStopIdQuery query
    ) {
        Tracking tracking = findTrackingByTripOrThrow(
                query.tripReferenceId()
        );

        return tracking.getStops().stream()
                .filter(stop ->
                        stop.getStopId().equals(query.stopId())
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Stop not found for this trip"
                        )
                );
    }

    private Tracking findTrackingByTripOrThrow(
            TripReferenceId tripReferenceId
    ) {
        return trackingRepository.findByTripReferenceId(
                        tripReferenceId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Tracking not found for this trip"
                        )
                );
    }
}