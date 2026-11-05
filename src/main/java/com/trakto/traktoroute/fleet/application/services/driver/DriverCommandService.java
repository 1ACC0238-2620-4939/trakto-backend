package com.trakto.traktoroute.fleet.application.services.driver;

import com.trakto.traktoroute.fleet.application.acl.profile.ProfileExistencePort;
import com.trakto.traktoroute.fleet.application.commands.driver.ActivateDriverCommand;
import com.trakto.traktoroute.fleet.application.commands.driver.ChangeDriverLicenseNumberCommand;
import com.trakto.traktoroute.fleet.application.commands.driver.CreateDriverCommand;
import com.trakto.traktoroute.fleet.application.commands.driver.DeactivateDriverCommand;
import com.trakto.traktoroute.fleet.domain.model.aggregates.Driver;
import com.trakto.traktoroute.fleet.domain.repositories.DriverRepository;
import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriverCommandService {

    private final DriverRepository driverRepository;
    private final ProfileExistencePort profileExistencePort;

    public DriverCommandService(DriverRepository driverRepository,
                                ProfileExistencePort profileExistencePort) {
        this.driverRepository = driverRepository;
        this.profileExistencePort = profileExistencePort;
    }

    @Transactional
    public Result<Driver, ApplicationError> handle(CreateDriverCommand command) {

        if (!profileExistencePort.profileExists(command.profileId().value())) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Profile",
                            command.profileId().value().toString()
                    )
            );
        }


        if (driverRepository.existsByProfileId(command.profileId())) {
            return Result.failure(
                    ApplicationError.conflict(
                            "Driver",
                            "A driver already exists for this profile"
                    )
            );
        }

        if (driverRepository.existsByLicenseNumber(command.licenseNumber())) {
            return Result.failure(
                    ApplicationError.conflict(
                            "Driver",
                            "License number is already registered"
                    )
            );
        }

        Driver driver = Driver.create(
                command.profileId(),
                command.licenseNumber()
        );

        Driver savedDriver = driverRepository.save(driver);
        return Result.success(savedDriver);
    }

    @Transactional
    public Result<Driver, ApplicationError> handle(
            ChangeDriverLicenseNumberCommand command) {

        var driverOptional = driverRepository.findById(command.driverId());

        if (driverOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Driver",
                            command.driverId().value().toString()
                    )
            );
        }

        Driver driver = driverOptional.get();

        if (driver.getLicenseNumber().equals(command.licenseNumber())) {
            return Result.success(driver);
        }

        if (driverRepository.existsByLicenseNumber(command.licenseNumber())) {
            return Result.failure(
                    ApplicationError.conflict(
                            "Driver",
                            "License number is already registered"
                    )
            );
        }

        driver.changeLicenseNumber(command.licenseNumber());

        Driver savedDriver = driverRepository.save(driver);
        return Result.success(savedDriver);
    }

    @Transactional
    public Result<Driver, ApplicationError> handle(ActivateDriverCommand command) {

        var driverOptional = driverRepository.findById(command.driverId());

        if (driverOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Driver",
                            command.driverId().value().toString()
                    )
            );
        }

        Driver driver = driverOptional.get();

        try {
            driver.activate();
        } catch (IllegalStateException e) {
            return Result.failure(
                    ApplicationError.businessRuleViolation(
                            "Driver activation",
                            e.getMessage()
                    )
            );
        }

        Driver savedDriver = driverRepository.save(driver);
        return Result.success(savedDriver);
    }

    @Transactional
    public Result<Driver, ApplicationError> handle(DeactivateDriverCommand command) {

        var driverOptional = driverRepository.findById(command.driverId());

        if (driverOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Driver",
                            command.driverId().value().toString()
                    )
            );
        }

        Driver driver = driverOptional.get();

        try {
            driver.deactivate();
        } catch (IllegalStateException e) {
            return Result.failure(
                    ApplicationError.businessRuleViolation(
                            "Driver deactivation",
                            e.getMessage()
                    )
            );
        }

        Driver savedDriver = driverRepository.save(driver);
        return Result.success(savedDriver);
    }
}