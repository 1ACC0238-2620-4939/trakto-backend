package com.trakto.traktoroute.fleet.interfaces.acl;

import java.util.UUID;

public interface FleetContextFacade {

    boolean isDriverActive(UUID driverId);

    boolean isVehicleActive(UUID vehicleId);
}