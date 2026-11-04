package com.trakto.traktoroute.trip.application.acl.fleet;

import java.util.UUID;

public interface FleetAvailabilityPort {

    boolean canAssignDriver(UUID driverId);

    boolean canAssignVehicle(UUID vehicleId);
}