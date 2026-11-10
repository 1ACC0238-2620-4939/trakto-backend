package com.trakto.traktoroute.tracking.infrastructure.persistence.jpa.adapters;

import com.trakto.traktoroute.tracking.domain.model.aggregates.Tracking;
import com.trakto.traktoroute.tracking.domain.model.valueobjects.TrackingId;
import com.trakto.traktoroute.tracking.domain.model.valueobjects.TripReferenceId;
import com.trakto.traktoroute.tracking.domain.repositories.TrackingRepository;
import com.trakto.traktoroute.tracking.infrastructure.persistence.jpa.assemblers.TrackingPersistenceAssembler;
import com.trakto.traktoroute.tracking.infrastructure.persistence.jpa.entities.TrackingPersistenceEntity;
import com.trakto.traktoroute.tracking.infrastructure.persistence.jpa.repositories.TrackingJpaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class TrackingRepositoryImpl implements TrackingRepository {

    private final TrackingJpaRepository trackingJpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TrackingRepositoryImpl(
            TrackingJpaRepository trackingJpaRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.trackingJpaRepository = trackingJpaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tracking> findById(TrackingId trackingId) {
        return trackingJpaRepository
                .findByTrackingId(trackingId)
                .map(TrackingPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tracking> findByTripReferenceId(
            TripReferenceId tripReferenceId
    ) {
        return trackingJpaRepository
                .findByTripReferenceId(tripReferenceId)
                .map(TrackingPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    @Transactional
    public Tracking save(Tracking tracking) {
        Optional<TrackingPersistenceEntity> existingEntity = trackingJpaRepository
                .findByTrackingId(tracking.getTrackingId());
        Tracking saved;
        if (existingEntity.isPresent()) {
            TrackingPersistenceEntity entity = existingEntity.get();
            TrackingPersistenceAssembler.updatePersistenceFromDomain(tracking,entity);

            TrackingPersistenceEntity savedEntity = trackingJpaRepository.save(entity);
            saved = TrackingPersistenceAssembler.toDomainFromPersistence(savedEntity);
        } else {
            TrackingPersistenceEntity newEntity = TrackingPersistenceAssembler.toPersistenceFromDomain(tracking);
            TrackingPersistenceEntity savedEntity = trackingJpaRepository.save(newEntity);
            saved = TrackingPersistenceAssembler.toDomainFromPersistence(savedEntity);
        }
        tracking.domainEvents().forEach(eventPublisher::publishEvent);
        tracking.clearDomainEvents();

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTripReferenceId(
            TripReferenceId tripReferenceId
    ) {
        return trackingJpaRepository
                .existsByTripReferenceId(tripReferenceId);
    }
}