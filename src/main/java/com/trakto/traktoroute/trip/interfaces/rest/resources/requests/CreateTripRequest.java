package com.trakto.traktoroute.trip.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "CreateTripRequest",
        description = "Request to create a new trip")
public record CreateTripRequest(

        @Schema(description = "Driver unique identifier",
                example = "550e8400-e29b-41d4-a716-446655440001")
        UUID driverId,

        @Schema(description = "Vehicle unique identifier",
                example = "550e8400-e29b-41d4-a716-446655440002")
        UUID vehicleId,

        @Schema(description = "Trip origin address",
                example = "Santa Anita, Lima")
        String originAddress,

        @Schema(description = "Trip origin latitude",
                example = "-12.043")
        BigDecimal originLatitude,

        @Schema(description = "Trip origin longitude",
                example = "-76.971")
        BigDecimal originLongitude,

        @Schema(description = "Trip destination address",
                example = "Miraflores, Lima")
        String destinationAddress,

        @Schema(description = "Trip destination latitude",
                example = "-12.121")
        BigDecimal destinationLatitude,

        @Schema(description = "Trip destination longitude",
                example = "-77.029")
        BigDecimal destinationLongitude,

        @Schema(description = "Scheduled trip start time",
                example = "2026-09-25T14:00:00Z")
        Instant scheduledAt,

        @Schema(description = "Date and time when the route was calculated",
                example = "2026-09-24T18:00:00Z")
        Instant calculatedAt
) {
}