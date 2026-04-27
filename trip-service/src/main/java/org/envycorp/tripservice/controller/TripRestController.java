package org.envycorp.tripservice.controller;

import org.envycorp.tripservice.model.Trip;
import org.envycorp.tripservice.model.TripInput;
import org.envycorp.tripservice.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripRestController {

    private final TripService tripService;

    @PostMapping
    public ResponseEntity<Trip> create(@Valid @RequestBody TripInput input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createTrip(input));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.getTripById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Trip>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(tripService.getTripsByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<List<Trip>> getAll() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }
}
