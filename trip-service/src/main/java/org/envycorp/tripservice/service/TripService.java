package org.envycorp.tripservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.envycorp.tripservice.entity.Trip;
import org.envycorp.tripservice.repository.TripRepository;
import org.envycorp.tripservice.dto.TripInput;
import org.springframework.kafka.core.KafkaTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final TripRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Trip createTrip(TripInput input) {
        log.info("Processing createTrip mutation for destination: {}", input.getDestination());

        Trip trip = Trip.builder()
                .destination(input.getDestination())
                .build();

        Trip savedTrip = repository.save(trip);

        try {
            kafkaTemplate.send("trip.created", savedTrip);
            log.info("Kafka event 'trip.created' sent for ID: {}", savedTrip.getId());
        } catch (Exception e) {
            log.error("Failed to send Kafka event for trip: {}", savedTrip.getId(), e);
        }

        return savedTrip;
    }

    @Transactional(readOnly = true)
    public Trip getById(Long id) {
        log.info("Processing getTrip query for ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trip with ID " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Trip> getByIds(List<Long> ids) {
        log.info("Processing trips fetch for multiple IDs: {}", ids);
        return repository.findAllByIdIn(ids);
    }

    @Transactional
    public Trip updateTrip(Long id, TripInput input) {
        log.info("Processing updateTrip mutation for ID: {}", id);

        Trip trip = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot update: Trip with ID " + id + " not found"));

        trip.setDestination(input.getDestination());

        Trip updatedTrip = repository.save(trip);
        log.info("Trip updated successfully: {}", updatedTrip.getId());

        return updatedTrip;
    }

    @Transactional
    public void deleteTrip(Long id) {
        log.info("Processing deleteTrip mutation for ID: {}", id);

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete: Trip with ID " + id + " not found");
        }

        repository.deleteById(id);
        log.info("Trip with ID {} deleted", id);
    }

    @Transactional(readOnly = true)
    public List<Trip> getAllTrips() {
        log.info("Processing getAllTrips query");
        return repository.findAll();
    }
}