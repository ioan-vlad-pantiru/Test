// src/main/java/com/example/transport/server/entity/Seat.java
package com.example.transport.server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "seats",
        uniqueConstraints = @UniqueConstraint(name = "idx_seat_ride", columnNames = {"ride_id","seat_number"}))
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ride_id", nullable = false)
    @JsonBackReference(value = "ride-seats")
    private Ride ride;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = true)
    @JsonBackReference(value = "reservation-seats")
    private Reservation reservation;

    public Seat() {}
    public Seat(Ride ride, int seatNumber) {
        this.ride = ride;
        this.seatNumber = seatNumber;
    }

    public Long getId() { return id; }

    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }

    public int getNumber() { return seatNumber; }
    public void setNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
}