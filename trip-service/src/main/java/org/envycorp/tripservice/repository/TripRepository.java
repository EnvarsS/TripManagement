package org.envycorp.tripservice.repository;

import org.envycorp.tripservice.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    // JpaRepository вже має методи save(), findAll(), delete() тощо.
    // Знайти всі поїздки, ID яких містяться в наданому списку
    List<Trip> findAllByIdIn(List<Long> ids);
}
