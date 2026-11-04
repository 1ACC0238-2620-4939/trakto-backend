package com.trakto.traktoroute.profiles.domain.model.aggregates;

import com.trakto.traktoroute.shared.domain.models.aggregates.AbstractDomainAggregateRoot;
import com.trakto.traktoroute.profiles.domain.model.events.ProfileCreatedEvent;
import com.trakto.traktoroute.profiles.domain.model.events.ProfileFullNameChangedEvent;
import com.trakto.traktoroute.profiles.domain.model.valueobjects.PersonName;
import com.trakto.traktoroute.profiles.domain.model.valueobjects.ProfileId;
import com.trakto.traktoroute.profiles.domain.model.valueobjects.UserReferenceId;

import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

@Getter
public class Profile extends AbstractDomainAggregateRoot<Profile> {

    private final ProfileId profileId;
    private final UserReferenceId userReferenceId;

    private PersonName personName;

    private Profile(
            ProfileId profileId,
            UserReferenceId userReferenceId,
            PersonName personName
    ) {
        this.profileId = Objects.requireNonNull(
                profileId, "Profile ID cannot be null"
        );

        this.userReferenceId = Objects.requireNonNull(
                userReferenceId, "User reference ID cannot be null"
        );

        this.personName = Objects.requireNonNull(
                personName, "Person name cannot be null"
        );
    }

    public static Profile create(
            UserReferenceId userReferenceId,
            PersonName personName
    ) {
        Profile profile = new Profile(
                ProfileId.generate(),
                userReferenceId,
                personName
        );

        profile.registerDomainEvent(
                new ProfileCreatedEvent(
                        profile.profileId,
                        profile.userReferenceId,
                        profile.personName,
                        Instant.now()
                )
        );

        return profile;
    }

    public static Profile reconstitute(
            ProfileId profileId,
            UserReferenceId userReferenceId,
            PersonName personName
    ) {
        return new Profile(
                profileId,
                userReferenceId,
                personName
        );
    }

    public void changeFullName(PersonName personName) {
        Objects.requireNonNull(
                personName,
                "Person name cannot be null"
        );

        if (this.personName.equals(personName)) {
            return;
        }

        PersonName previousPersonName = this.personName;
        this.personName = personName;

        registerDomainEvent(
                new ProfileFullNameChangedEvent(
                        profileId,
                        previousPersonName,
                        personName,
                        Instant.now()
                )
        );
    }
}