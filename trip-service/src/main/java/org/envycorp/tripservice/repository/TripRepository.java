package org.envycorp.tripservice.repository;

import org.envycorp.tripservice.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserId(Long userId);
}