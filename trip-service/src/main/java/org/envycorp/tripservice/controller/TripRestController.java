package org.envycorp.tripservice.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.envycorp.tripservice.entity.Trip;
import org.envycorp.tripservice.dto.TripInput;
import org.envycorp.tripservice.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripRestController {
    private final TripService tripService;

    @PostMapping
    public Trip create(@RequestBody TripInput input) {
        return tripService.createTrip(input);
    }

    @GetMapping("/trip/{id}")
    public Trip getById(@PathVariable Long id) {
        return tripService.getById(id);
    }

    @GetMapping("/batch")               // /trips/batch?ids=10,11,12
    public List<Trip> getBatch(@RequestParam List<Long> ids) {
        return tripService.getByIds(ids);
    }

    @GetMapping
    public List<Trip> getAll() {
        return tripService.getAllTrips();
    }

    @PutMapping("/trip/{id}")
    public Trip update(@PathVariable Long id, @RequestBody TripInput input) {
        return tripService.updateTrip(id, input);
    }

    @DeleteMapping("/trip/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok(true);
    }
}
