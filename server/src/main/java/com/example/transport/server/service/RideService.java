// src/main/java/com/example/transport/server/service/RideService.java
package com.example.transport.server.service;

import com.example.transport.server.entity.Ride;
import com.example.transport.server.repository.RideRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RideService {

    private final RideRepository rideRepo;

    public RideService(RideRepository rideRepo) {
        this.rideRepo = rideRepo;
    }

    /**
     * Create a new Ride. Any incoming ID is ignored so that the database will generate one.
     */
    public Ride createRide(Ride ride) {
        ride.setId(null);
        return rideRepo.save(ride);
    }

    /**
     * Update an existing Ride. Returns the updated entity, or null if not found.
     */
    public Ride updateRide(Long id, Ride newData) {
        Ride existing = rideRepo.findById(id);
        if (existing == null) {
            return null;
        }
        // Only update fields if provided
        if (newData.getDestination() != null) {
            existing.setDestination(newData.getDestination());
        }
        if (newData.getDepartureTime() != null) {
            existing.setDepartureTime(newData.getDepartureTime());
        }
        // Note: seats are managed via SeatService/reservations; do not overwrite here
        return rideRepo.save(existing);
    }

    /**
     * Delete a Ride by its ID. Returns true if deletion succeeded, false if not found.
     */
    public boolean deleteRide(Long id) {
        return rideRepo.deleteById(id);
    }

    /**
     * Retrieve a Ride by its ID, or null if not found.
     */
    @Transactional(value = TxType.SUPPORTS)
    public Ride getRideById(Long id) {
        return rideRepo.findById(id);
    }

    /**
     * List all rides, optionally filtered by destination substring,
     * with optional pagination parameters.
     *
     * @param destinationFilter case‐insensitive substring to filter by destination; if null or empty, returns all rides.
     * @param page zero‐based page index; if null or negative, no paging is applied.
     * @param size maximum number of items per page; if null or non‐positive, no paging is applied.
     */
    @Transactional(value = TxType.SUPPORTS)
    public List<Ride> listRides(String destinationFilter, Integer page, Integer size) {
        return rideRepo.findAll(destinationFilter, page, size);
    }

    public List<Ride> getAllRides() {
        return rideRepo.findAll(null, null, null);
    }

    public List<Ride> searchRides(String destination, LocalDate dateFilter) {
        if (dateFilter != null) {
            return rideRepo.findByDestinationAndDate(destination, dateFilter);
        } else {
            return rideRepo.findByDestination(destination);
        }
    }
}