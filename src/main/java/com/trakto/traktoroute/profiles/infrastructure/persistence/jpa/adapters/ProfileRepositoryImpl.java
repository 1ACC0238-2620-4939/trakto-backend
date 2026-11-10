package com.trakto.traktoroute.profiles.infrastructure.persistence.jpa.adapters;

import com.trakto.traktoroute.profiles.domain.model.aggregates.Profile;
import com.trakto.traktoroute.profiles.domain.model.valueobjects.ProfileId;
import com.trakto.traktoroute.profiles.domain.model.valueobjects.UserReferenceId;
import com.trakto.traktoroute.profiles.domain.repositories.ProfileRepository;
import com.trakto.traktoroute.profiles.infrastructure.persistence.jpa.assemblers.ProfilePersistenceAssembler;
import com.trakto.traktoroute.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import com.trakto.traktoroute.profiles.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

    private final ProfilePersistenceRepository profilePersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProfileRepositoryImpl(
            ProfilePersistenceRepository profilePersistenceRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.profilePersistenceRepository = profilePersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findById(ProfileId profileId) {
        return profilePersistenceRepository
                .findByProfileId(profileId)
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findByUserReferenceId(
            UserReferenceId userReferenceId
    ) {
        return profilePersistenceRepository
                .findByUserReferenceId(userReferenceId)
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Profile> findAll() {
        return profilePersistenceRepository
                .findAll()
                .stream()
                .map(ProfilePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    @Transactional
    public Profile save(Profile profile) {

        Optional<ProfilePersistenceEntity> existingEntity = profilePersistenceRepository
                .findByProfileId(profile.getProfileId());
        Profile saved;
        if (existingEntity.isPresent()) {
            ProfilePersistenceEntity entity = existingEntity.get();
            ProfilePersistenceAssembler.updatePersistenceFromDomain(profile, entity);

            ProfilePersistenceEntity savedEntity = profilePersistenceRepository.save(entity);
            saved = ProfilePersistenceAssembler.toDomainFromPersistence(savedEntity);

        } else {
            ProfilePersistenceEntity newEntity = ProfilePersistenceAssembler.toPersistenceFromDomain(profile);
            ProfilePersistenceEntity savedEntity = profilePersistenceRepository.save(newEntity);
            saved = ProfilePersistenceAssembler.toDomainFromPersistence(savedEntity);
        }
        profile.domainEvents().forEach(eventPublisher::publishEvent);
        profile.clearDomainEvents();

        return saved;

    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(ProfileId profileId) {
        return profilePersistenceRepository.existsByProfileId(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserReferenceId(
            UserReferenceId userReferenceId
    ) {
        return profilePersistenceRepository.existsByUserReferenceId(
                userReferenceId
        );
    }
}