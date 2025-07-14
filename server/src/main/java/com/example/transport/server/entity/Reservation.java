// src/main/java/com/example/transport/server/entity/Reservation.java
package com.example.transport.server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ride_id", nullable = false)
    @JsonBackReference
    private Ride ride;

    @Column(name = "customer_name", length = 100, nullable = false)
    private String customerName;

    @Column(name = "seats_reserved", nullable = false)
    private int seatsReserved;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Seat> seats = new ArrayList<>();

    public Reservation() {}
    public Reservation(Ride ride, String customerName, int seatsReserved) {
        this.ride = ride;
        this.customerName = customerName;
        this.seatsReserved = seatsReserved;
    }

    public Long getId() { return id; }

    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getSeatsBooked() { return seatsReserved; }
    public void setSeatsReserved(int seatsReserved) { this.seatsReserved = seatsReserved; }

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
}