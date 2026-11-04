package com.trakto.traktoroute.fleet.application.acl;

import com.trakto.traktoroute.fleet.interfaces.acl.FleetContextFacade;
import com.trakto.traktoroute.fleet.application.services.driver.DriverQueryService;
import com.trakto.traktoroute.fleet.application.services.vehicle.VehicleQueryService;
import com.trakto.traktoroute.fleet.application.queries.driver.GetDriverByIdQuery;
import com.trakto.traktoroute.fleet.application.queries.vehicle.GetVehicleByIdQuery;
import com.trakto.traktoroute.fleet.domain.model.valueobjects.driver.DriverId;
import com.trakto.traktoroute.fleet.domain.model.valueobjects.vehicle.VehicleId;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FleetContextFacadeImpl implements FleetContextFacade {

    private final DriverQueryService driverQueryService;
    private final VehicleQueryService vehicleQueryService;

    public FleetContextFacadeImpl(
            DriverQueryService driverQueryService,
            VehicleQueryService vehicleQueryService
    ) {
        this.driverQueryService = driverQueryService;
        this.vehicleQueryService = vehicleQueryService;
    }

    @Override
    public boolean isDriverActive(UUID driverId) {

        var query = new GetDriverByIdQuery(
                new DriverId(driverId)
        );

        return driverQueryService.handle(query)
                .map(driver -> driver.isActive())
                .orElse(false);
    }

    @Override
    public boolean isVehicleActive(UUID vehicleId) {

        var query = new GetVehicleByIdQuery(
                new VehicleId(vehicleId)
        );

        return vehicleQueryService.handle(query)
                .map(vehicle -> vehicle.isActive())
                .orElse(false);
    }
}