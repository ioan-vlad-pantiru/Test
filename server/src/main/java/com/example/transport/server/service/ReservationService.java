package com.example.transport.server.service;

import com.example.transport.server.entity.Ride;
import com.example.transport.server.entity.User;
import com.example.transport.server.entity.Seat;
import com.example.transport.server.entity.Reservation;
import com.example.transport.server.repository.RideRepository;
import com.example.transport.server.repository.UserRepository;
import com.example.transport.server.repository.SeatRepository;
import com.example.transport.server.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ReservationService {
    private final RideRepository rideRepo;
    private final UserRepository userRepo;
    private final SeatRepository seatRepo;
    private final ReservationRepository reservationRepo;

    public ReservationService(RideRepository rideRepo, UserRepository userRepo,
                              SeatRepository seatRepo, ReservationRepository reservationRepo) {
        this.rideRepo = rideRepo;
        this.userRepo = userRepo;
        this.seatRepo = seatRepo;
        this.reservationRepo = reservationRepo;
    }

    public synchronized boolean reserveSeats(Long rideId, String customerName, int seatCount, String username) {
        // Find the ride and user (if tracking which user made the booking)
        Ride ride = rideRepo.findById(rideId);
        if (ride == null) {
            return false;
        }
        User user = null;
        if (username != null) {
            user = userRepo.findByUsername(username);
        }
        // Check available seats
        List<Seat> freeSeats = seatRepo.findFreeSeatsByRideId(rideId);
        if (freeSeats.size() < seatCount) {
            return false;  // not enough available seats
        }
        // Create reservation record
        Reservation reservation = new Reservation(ride, customerName, seatCount);
        reservation = reservationRepo.save(reservation);
        // Assign seats to this reservation
        for (int i = 0; i < seatCount; i++) {
            Seat seat = freeSeats.get(i);
            seat.setReservation(reservation);
        }
        // When the transaction commits, the Seat entities will be updated in DB
        return true;
    }
}