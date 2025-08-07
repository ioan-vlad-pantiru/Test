// src/main/java/com/example/transport/server/dto/RideDto.java
package com.example.transport.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class RideDto {
    private Long id;
    private String destination;

    @JsonFormat(pattern = "MM-yyyy-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;

    public RideDto() {}

    public RideDto(Long idx, String destination, LocalDateTime departureTime) {
        this.idx = idx;
        this.destination = destination;
        this.departureTime = departureTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
}