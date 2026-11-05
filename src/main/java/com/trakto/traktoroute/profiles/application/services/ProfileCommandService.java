package com.trakto.traktoroute.profiles.application.services;

import com.trakto.traktoroute.shared.application.result.ApplicationError;
import com.trakto.traktoroute.shared.application.result.Result;
import com.trakto.traktoroute.profiles.application.commands.ChangeProfileFullNameCommand;
import com.trakto.traktoroute.profiles.application.commands.CreateProfileCommand;
import com.trakto.traktoroute.profiles.domain.model.aggregates.Profile;
import com.trakto.traktoroute.profiles.domain.model.valueobjects.ProfileId;
import com.trakto.traktoroute.profiles.domain.repositories.ProfileRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileCommandService {

    private final ProfileRepository profileRepository;

    public ProfileCommandService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional
    public Result<ProfileId, ApplicationError> handle(
            CreateProfileCommand command
    ) {
        if (profileRepository.existsByUserReferenceId(
                command.userReferenceId()
        )) {
            return Result.failure(
                    ApplicationError.conflict(
                            "Profile",
                            "A profile already exists for this user"
                    )
            );
        }

        Profile profile;

        try {
            profile = Profile.create(
                    command.userReferenceId(),
                    command.personName()
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.failure(
                    ApplicationError.validationError(
                            "Profile",
                            e.getMessage()
                    )
            );
        }

        Profile savedProfile = profileRepository.save(profile);

        return Result.success(savedProfile.getProfileId());
    }

    @Transactional
    public Result<Void, ApplicationError> handle(
            ChangeProfileFullNameCommand command
    ) {
        var profileOptional = profileRepository.findById(
                command.profileId()
        );

        if (profileOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Profile",
                            command.profileId().value().toString()
                    )
            );
        }

        Profile profile = profileOptional.get();

        if (profile.getPersonName().equals(command.personName())) {
            return Result.success(null);
        }

        try {
            profile.changeFullName(command.personName());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.failure(
                    ApplicationError.validationError(
                            "Profile",
                            e.getMessage()
                    )
            );
        }

        profileRepository.save(profile);

        return Result.success(null);
    }
}