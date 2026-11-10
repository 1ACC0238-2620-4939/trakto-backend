package com.trakto.traktoroute.fleet.infrastructure.persistence.jpa.adapters.driver;

import com.trakto.traktoroute.fleet.domain.model.aggregates.Driver;
import com.trakto.traktoroute.fleet.domain.model.enums.DriverStatus;
import com.trakto.traktoroute.fleet.domain.model.valueobjects.driver.DriverId;
import com.trakto.traktoroute.fleet.domain.model.valueobjects.driver.LicenseNumber;
import com.trakto.traktoroute.fleet.domain.model.valueobjects.driver.ProfileId;
import com.trakto.traktoroute.fleet.domain.repositories.DriverRepository;
import com.trakto.traktoroute.fleet.infrastructure.persistence.jpa.assemblers.driver.DriverPersistenceAssembler;
import com.trakto.traktoroute.fleet.infrastructure.persistence.jpa.entities.driver.DriverPersistenceEntity;
import com.trakto.traktoroute.fleet.infrastructure.persistence.jpa.repositories.driver.DriverPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DriverRepositoryIml implements DriverRepository {

    private final DriverPersistenceRepository driverPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DriverRepositoryIml(DriverPersistenceRepository driverPersistenceRepository,
                               ApplicationEventPublisher eventPublisher) {
        this.driverPersistenceRepository = driverPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Driver save(Driver driver) {

        Optional<DriverPersistenceEntity> existingEntity = driverPersistenceRepository.findByDriverId(driver.getId());

        Driver saved;

        if (existingEntity.isPresent()) {
            var entity = existingEntity.get();

            DriverPersistenceAssembler.updatePersistenceFromDomain(
                    driver,
                    entity);

            var savedEntity = driverPersistenceRepository.save(entity);
            saved = DriverPersistenceAssembler.toDomainFromPersistence(savedEntity);
        } else {
            var newEntity = DriverPersistenceAssembler.toPersistenceFromDomain(driver);
            var savedEntity = driverPersistenceRepository.save(newEntity);
            saved = DriverPersistenceAssembler.toDomainFromPersistence(savedEntity);

        }
        driver.domainEvents().forEach(eventPublisher::publishEvent);
        driver.clearDomainEvents();

        return saved;

    }


    @Override
    public Optional<Driver> findById(DriverId driverId) {
        return driverPersistenceRepository.findByDriverId(driverId)
                .map(DriverPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Driver> findByProfileId(ProfileId profileId) {
        return driverPersistenceRepository.findByProfileId(profileId)
                .map(DriverPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Driver> findAll() {
        return driverPersistenceRepository.findAll()
                .stream()
                .map(DriverPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Driver> findByStatus(DriverStatus status) {
        return driverPersistenceRepository.findByStatus(status)
                .stream()
                .map(DriverPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsById(DriverId driverId) {
        return driverPersistenceRepository.existsByDriverId(driverId);
    }

    @Override
    public boolean existsByProfileId(ProfileId profileId) {
        return driverPersistenceRepository.existsByProfileId(profileId);
    }

    @Override
    public boolean existsByLicenseNumber(LicenseNumber licenseNumber) {
        return driverPersistenceRepository.existsByLicenseNumber(licenseNumber);
    }
}