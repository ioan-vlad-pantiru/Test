// src/main/java/com/example/transport/client/Ride.java
package com.example.transport.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class Ride {
    private Long id;
    private String destination;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;

    public Ride() { }

    public Ride(String destination, LocalDateTime departureTime) {
        this.destination = destination;
        this.departureTime = departureTime;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "id=" + id +
                ", destination='" + destination + '\'' +
                ", departureTime=" + departureTime +
                '}';
    }
}