package com.example.transport.server.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rides")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "destination", length = 100, nullable = false)
    private String destination;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime departureTime;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Seat> seats = new ArrayList<>();

    public Ride() {}
    public Ride(String destination, LocalDateTime departureTime) {
        this.destination = destination;
        this.departureTime = departureTime;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }

    public List<Seat> getSeatsAvailable() {
        List<Seat> availableSeats = new ArrayList<>();
        for (Seat seat : seats) {
            if (seat.getReservation() == null) {
                availableSeats.add(seat);
            }
        }
        return availableSeats;
    }
}