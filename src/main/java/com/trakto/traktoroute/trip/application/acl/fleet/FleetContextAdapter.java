package com.trakto.traktoroute.trip.application.acl.fleet;

import com.trakto.traktoroute.fleet.interfaces.acl.FleetContextFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FleetContextAdapter implements FleetAvailabilityPort {

    private final FleetContextFacade fleetContextFacade;

    public FleetContextAdapter(
            FleetContextFacade fleetContextFacade
    ) {
        this.fleetContextFacade = fleetContextFacade;
    }

    @Override
    public boolean canAssignDriver(UUID driverId) {
        return fleetContextFacade.isDriverActive(driverId);
    }

    @Override
    public boolean canAssignVehicle(UUID vehicleId) {
        return fleetContextFacade.isVehicleActive(vehicleId);
    }
}