package org.envycorp.tripservice.service;

import org.envycorp.tripservice.client.UserClient;
import org.envycorp.tripservice.exception.ResourceNotFoundException;
import org.envycorp.tripservice.model.Trip;
import org.envycorp.tripservice.model.TripInput;
import org.envycorp.tripservice.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final TripRepository tripRepository;
    private final UserClient userClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public Trip createTrip(TripInput input) {
        log.info("Отримано запит на створення поїздки для користувача #{}", input.getUserId());

        // Синхронна перевірка користувача через REST/Feign
        Boolean userExists = userClient.checkUserExists(input.getUserId());
        if (!Boolean.TRUE.equals(userExists)) {
            throw new ResourceNotFoundException("Користувача з ID " + input.getUserId() + " не знайдено");
        }

        Trip trip = Trip.builder()
                .userId(input.getUserId())
                .origin(input.getOrigin())
                .destination(input.getDestination())
                .departureTime(LocalDateTime.parse(input.getDepartureTime()))
                .status("PLANNED")
                .build();

        Trip saved = tripRepository.save(trip);

        // Асинхронне повідомлення в Kafka
        kafkaTemplate.send("trip-events", "TRIP_CREATED:" + saved.getId());
        return saved;
    }

    public Trip getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Поїздку з ID " + id + " не знайдено"));
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public List<Trip> getTripsByUserId(Long userId) {
        return tripRepository.findByUserId(userId);
    }
}
