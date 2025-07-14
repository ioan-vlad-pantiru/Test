// src/main/java/com/example/transport/client/RideClient.java
package com.example.transport.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.StringJoiner;

public class RideClient {
    private final HttpClient http;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public RideClient(String baseUrl) {
        this.http = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper()
                .findAndRegisterModules(); // registers JavaTimeModule for LocalDateTime
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl + "api/rides" : baseUrl + "/api/rides";
    }

    /** Create a ride; returns the created Ride (with generated ID). */
    public Ride createRide(Ride ride) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(ride);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 201) {
            throw new RuntimeException("Failed to create ride: " + resp.body());
        }
        return mapper.readValue(resp.body(), Ride.class);
    }

    /** Retrieve a ride by ID, or returns null if not found (404). */
    public Ride getRide(Long id) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + id))
                .GET()
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 404) return null;
        if (resp.statusCode() != 200) {
            throw new RuntimeException("Failed to get ride: " + resp.body());
        }
        return mapper.readValue(resp.body(), Ride.class);
    }

    /** Update an existing ride; returns the updated Ride or null if not found. */
    public Ride updateRide(Long id, Ride ride) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(ride);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 404) return null;
        if (resp.statusCode() != 200) {
            throw new RuntimeException("Failed to update ride: " + resp.body());
        }
        return mapper.readValue(resp.body(), Ride.class);
    }

    /** Delete a ride by ID; returns true if deleted, false if not found. */
    public boolean deleteRide(Long id) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + id))
                .DELETE()
                .build();
        HttpResponse<Void> resp = http.send(req, HttpResponse.BodyHandlers.discarding());
        if (resp.statusCode() == 404) return false;
        if (resp.statusCode() != 204) {
            throw new RuntimeException("Failed to delete ride, status: " + resp.statusCode());
        }
        return true;
    }

    /**
     * List rides, with optional destination filter and pagination.
     * Pass `null` for any parameter you want to omit.
     */
    public List<Ride> listRides(String destinationFilter, Integer page, Integer size)
            throws IOException, InterruptedException {
        String url = baseUrl;
        StringJoiner params = new StringJoiner("&");
        if (destinationFilter != null) params.add("destination=" + destinationFilter);
        if (page != null)               params.add("page=" + page);
        if (size != null)               params.add("size=" + size);
        if (params.length() > 0) {
            url += "?" + params.toString();
        }
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new RuntimeException("Failed to list rides: " + resp.body());
        }
        return mapper.readValue(resp.body(), new TypeReference<List<Ride>>() {});
    }

    /** Example main() demonstrating usage. */
    public static void main(String[] args) throws Exception {
        RideClient client = new RideClient("http://localhost:8080");

        // 1. Create
        Ride created = client.createRide(new Ride("London", java.time.LocalDateTime.of(2025,5,25,9,0)));
        System.out.println("Created: " + created);

        // 2. Get
        Ride fetched = client.getRide(created.getId());
        System.out.println("Fetched: " + fetched);

        // 3. Update
        fetched.setDestination("Manchester");
        Ride updated = client.updateRide(fetched.getId(), fetched);
        System.out.println("Updated: " + updated);

        // 4. List all
        System.out.println("All rides: " + client.listRides(null, null, null));

        // 5. Delete
        boolean deleted = client.deleteRide(updated.getId());
        System.out.println("Deleted: " + deleted);
    }
}