package com.trakto.traktoroute.fleet.infrastructure.persistence.jpa.adapters.vehicle;

import com.trakto.traktoroute.fleet.domain.model.aggregates.Vehicle;
import com.trakto.traktoroute.fleet.domain.model.enums.VehicleStatus;
import com.trakto.traktoroute.fleet.domain.model.valueobjects.vehicle.PlateNumber;
import com.trakto.traktoroute.fleet.domain.model.valueobjects.vehicle.VehicleId;
import com.trakto.traktoroute.fleet.domain.repositories.VehicleRepository;
import com.trakto.traktoroute.fleet.infrastructure.persistence.jpa.assemblers.vehicle.VehiclePersistenceAssembler;
import com.trakto.traktoroute.fleet.infrastructure.persistence.jpa.repositories.vehicle.VehiclePersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VehicleRepositoryIml implements VehicleRepository {

    private final VehiclePersistenceRepository vehicleRepositoryIml;
    private final ApplicationEventPublisher eventPublisher;

    public VehicleRepositoryIml(VehiclePersistenceRepository vehicleRepositoryIml,
                                ApplicationEventPublisher eventPublisher) {
        this.vehicleRepositoryIml = vehicleRepositoryIml;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {

        var existingEntity = vehicleRepositoryIml.findByVehicleId(vehicle.getId());

        Vehicle saved;

        if (existingEntity.isPresent()) {

            var entity = existingEntity.get();
            VehiclePersistenceAssembler.updatePersistenceFromDomain(
                    vehicle,
                    entity
            );

            var savedEntity = vehicleRepositoryIml.save(entity);
            saved = VehiclePersistenceAssembler.toDomainFromPersistence(savedEntity);
        }else {
            var newEntity = VehiclePersistenceAssembler.toPersistenceFromDomain(vehicle);
            var savedEntity = vehicleRepositoryIml.save(newEntity);
            saved = VehiclePersistenceAssembler.toDomainFromPersistence(savedEntity);
        }

        vehicle.domainEvents().forEach(eventPublisher::publishEvent);
        vehicle.clearDomainEvents();
        return saved;
    }

    @Override
    public Optional<Vehicle> findById(VehicleId vehicleId) {
        return vehicleRepositoryIml
                .findByVehicleId(vehicleId)
                .map(VehiclePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleRepositoryIml
                .findAll()
                .stream()
                .map(VehiclePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Vehicle> findByStatus(VehicleStatus status) {
        return vehicleRepositoryIml
                .findByStatus(status)
                .stream()
                .map(VehiclePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsById(VehicleId vehicleId) {
        return vehicleRepositoryIml.existsByVehicleId(vehicleId);
    }

    @Override
    public boolean existsByPlateNumber(PlateNumber plateNumber) {
        return vehicleRepositoryIml.existsByPlateNumber(plateNumber);
    }
}