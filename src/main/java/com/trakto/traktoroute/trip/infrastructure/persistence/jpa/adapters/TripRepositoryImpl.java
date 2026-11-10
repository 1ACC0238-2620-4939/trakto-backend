package com.trakto.traktoroute.trip.infrastructure.persistence.jpa.adapters;

import com.trakto.traktoroute.trip.domain.model.aggregates.Trip;
import com.trakto.traktoroute.trip.domain.model.enums.TripStatus;
import com.trakto.traktoroute.trip.domain.model.valueobjects.TripId;
import com.trakto.traktoroute.trip.domain.repositories.TripRepository;
import com.trakto.traktoroute.trip.infrastructure.persistence.jpa.assemblers.TripPersistenceAssembler;
import com.trakto.traktoroute.trip.infrastructure.persistence.jpa.entities.TripPersistenceEntity;
import com.trakto.traktoroute.trip.infrastructure.persistence.jpa.repositories.TripPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class TripRepositoryImpl implements TripRepository {

    private final TripPersistenceRepository tripPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TripRepositoryImpl(TripPersistenceRepository tripPersistenceRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.tripPersistenceRepository = tripPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trip> findById(TripId tripId) {
        return tripPersistenceRepository
                .findByTripId(tripId)
                .map(TripPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trip> findAll() {
        return tripPersistenceRepository
                .findAll()
                .stream()
                .map(TripPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trip> findByStatus(TripStatus status) {
        return tripPersistenceRepository
                .findByStatus(status)
                .stream()
                .map(TripPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    @Transactional
    public Trip save(Trip trip) {
        Optional<TripPersistenceEntity> existingEntity =
                tripPersistenceRepository.findByTripId(trip.getId());

        Trip saved;

        if (existingEntity.isPresent()) {
            TripPersistenceEntity entity = existingEntity.get();
            TripPersistenceAssembler.updatePersistenceFromDomain(trip, entity);

            TripPersistenceEntity savedEntity = tripPersistenceRepository.save(entity);
            saved = TripPersistenceAssembler.toDomainFromPersistence(savedEntity);
        } else {
            TripPersistenceEntity newEntity =
                    TripPersistenceAssembler.toPersistenceFromDomain(trip);

            TripPersistenceEntity savedEntity = tripPersistenceRepository.save(newEntity);
            saved = TripPersistenceAssembler.toDomainFromPersistence(savedEntity);
        }

        trip.domainEvents().forEach(eventPublisher::publishEvent);
        trip.clearDomainEvents();

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(TripId tripId) {
        return tripPersistenceRepository.existsByTripId(tripId);
    }
}