// src/test/java/com/example/transport/RideClient.java
package com.example.transport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class RideClient {
    private static final String BASE = "[http://localhost:8080/api/rides](http://localhost:8080/api/rides)";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        // Create a ride
        Ride newRide = new Ride(null, "Sibiu", LocalDateTime.of(2025, 6, 5, 8, 30));
        String json = mapper.writeValueAsString(newRide);
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create(BASE))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> postRes = client.send(post, HttpResponse.BodyHandlers.ofString());
        System.out.println("Create: " + postRes.statusCode() + " Body=" + postRes.body());

        // Get all
        HttpRequest getAll = HttpRequest.newBuilder()
                .uri(URI.create(BASE))
                .GET()
                .build();
        HttpResponse<String> getRes = client.send(getAll, HttpResponse.BodyHandlers.ofString());
        List<Ride> rides = mapper.readValue(getRes.body(), new TypeReference<>() {
        });
        System.out.println("All rides: " + rides);
    }

    public static class Ride {
        public Long id;
        public String destination;
        public LocalDateTime departureTime;

        public Ride() {
        }

        public Ride(Long id, String dest, LocalDateTime dt) {
            this.id = id;
            this.destination = dest;
            this.departureTime = dt;
        }

        public String toString() {
            return String.format("Ride[id=%d,dest=%s,dt=%s]", id, destination, departureTime);
        }
    }
}