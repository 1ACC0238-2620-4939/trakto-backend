package com.trakto.traktoroute.trip.interfaces.rest.transform;

import com.trakto.traktoroute.trip.application.commands.CreateTripCommand;
import com.trakto.traktoroute.trip.domain.model.valueobjects.DriverId;
import com.trakto.traktoroute.trip.domain.model.valueobjects.TripLocation;
import com.trakto.traktoroute.trip.domain.model.valueobjects.TripRoutePlan;
import com.trakto.traktoroute.trip.domain.model.valueobjects.TripSchedule;
import com.trakto.traktoroute.trip.domain.model.valueobjects.VehicleId;
import com.trakto.traktoroute.trip.interfaces.rest.resources.requests.CreateTripRequest;

public class CreateTripCommandFromRequestAssembler {

    private CreateTripCommandFromRequestAssembler() {}

    public static CreateTripCommand toCommand(CreateTripRequest request) {

        var driverId = new DriverId(request.driverId());

        var vehicleId = new VehicleId(request.vehicleId());

        var origin = new TripLocation(
                request.originAddress(),
                request.originLatitude(),
                request.originLongitude());

        var destination = new TripLocation(
                request.destinationAddress(),
                request.destinationLatitude(),
                request.destinationLongitude());

        var schedule = new TripSchedule(
                request.scheduledAt(),
                null,
                null,
                null);


        return new CreateTripCommand(driverId,
                                    vehicleId,
                                    origin,
                                    destination,
                                    schedule);
    }
}