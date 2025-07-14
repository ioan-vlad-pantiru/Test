package com.example.transport.server.controller;

import com.example.transport.server.dto.RideDto;
import com.example.transport.server.entity.Ride;
import com.example.transport.server.service.RideService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    private RideDto toDto(Ride r) {
        return new RideDto(r.getId(), r.getDestination(), r.getDepartureTime());
    }

    private Ride fromDto(RideDto dto) {
        Ride r = new Ride();
        r.setDestination(dto.getDestination());
        r.setDepartureTime(dto.getDepartureTime());
        return r;
    }

    @PostMapping
    public ResponseEntity<RideDto> create(@RequestBody RideDto dto) {
        Ride saved = rideService.createRide(fromDto(dto));
        RideDto out = toDto(saved);
        URI loc = URI.create("/api/rides/" + out.getId());
        return ResponseEntity.created(loc).body(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideDto> getOne(@PathVariable Long id) {
        Ride r = rideService.getRideById(id);
        if (r == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(r));
    }

    @GetMapping
    public List<RideDto> list(
            @RequestParam(name="destination", required=false) String destFilter,
            @RequestParam(name="page",        required=false) Integer page,
            @RequestParam(name="size",        required=false) Integer size
    ) {
        return rideService
                .listRides(destFilter, page, size)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideDto> update(
            @PathVariable Long id,
            @RequestBody RideDto dto
    ) {
        Ride updated = rideService.updateRide(id, fromDto(dto));
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean ok = rideService.deleteRide(id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}