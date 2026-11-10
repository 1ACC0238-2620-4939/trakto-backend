package com.trakto.traktoroute.trip.application.commands;

import com.trakto.traktoroute.trip.domain.model.valueobjects.*;

public record CreateTripCommand(
        DriverId driverId,
        VehicleId vehicleId,
        TripLocation origin,
        TripLocation destination,
        TripSchedule schedule) {
}