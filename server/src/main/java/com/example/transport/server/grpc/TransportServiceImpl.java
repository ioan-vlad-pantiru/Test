package com.example.transport.server.grpc;

import com.example.transport.server.entity.User;
import com.example.transport.server.entity.Ride;
import com.example.transport.common.proto.Seat;
import com.example.transport.server.service.AuthService;
import com.example.transport.server.service.RideService;
import com.example.transport.server.service.ReservationService;
import com.example.transport.common.proto.*;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransportServiceImpl extends TransportServiceGrpc.TransportServiceImplBase {
    private final AuthService authService;
    private final RideService rideService;
    private final ReservationService reservationService;
    // List of subscribers for real-time updates
    private final List<StreamObserver<RideUpdate>> subscribers = new ArrayList<>();

    public TransportServiceImpl(AuthService authService, RideService rideService, ReservationService reservationService) {
        this.authService = authService;
        this.rideService = rideService;
        this.reservationService = reservationService;
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        String username = request.getUsername();
        String password = request.getPassword();
        User user = authService.login(username, password);
        LoginResponse reply;
        if (user != null) {
            reply = LoginResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Login successful")
                    .build();
        } else {
            reply = LoginResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Invalid username or password")
                    .build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void listRides(com.google.protobuf.Empty request, StreamObserver<ListRidesResponse> responseObserver) {
        List<Ride> rides = rideService.getAllRides();
        ListRidesResponse.Builder responseBuilder = ListRidesResponse.newBuilder();
        for (Ride ride : rides) {
            responseBuilder.addRides(convertRideEntityToProto(ride));
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void searchRides(SearchRequest request, StreamObserver<ListRidesResponse> responseObserver) {
        String destination = request.getDestination();
        String dateStr = request.getDate();
        LocalDate dateFilter = (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : null;
        List<Ride> rides = rideService.searchRides(destination, dateFilter);
        ListRidesResponse.Builder responseBuilder = ListRidesResponse.newBuilder();
        for (Ride ride : rides) {
            responseBuilder.addRides(convertRideEntityToProto(ride));
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void reserveSeat(ReserveRequest request, StreamObserver<ReserveResponse> responseObserver) {
        Long rideId = request.getRideId();
        String customerName = request.getCustomerName();
        int seats = request.getSeatCount();
        // We could include the username of the agent in the request for auditing; here we pass null
        boolean success = reservationService.reserveSeats(rideId, customerName, seats, null);
        ReserveResponse reply;
        if (success) {
            reply = ReserveResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Reservation successful")
                    .build();
            // Broadcast update to all subscribed clients about this ride
            Ride updatedRide = rideService.getRideById(rideId);
            RideUpdate update = RideUpdate.newBuilder()
                    .setRide(convertRideEntityToProto(updatedRide))
                    .build();
            synchronized (subscribers) {
                for (StreamObserver<RideUpdate> sub : subscribers) {
                    sub.onNext(update);
                }
            }
        } else {
            reply = ReserveResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Reservation failed")
                    .build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void subscribeRideUpdates(com.google.protobuf.Empty request, StreamObserver<RideUpdate> responseObserver) {
        // Add new client subscriber for updates
        synchronized (subscribers) {
            subscribers.add(responseObserver);
        }
        // Remove the subscriber if the client disconnects
        if (responseObserver instanceof io.grpc.stub.ServerCallStreamObserver) {
            io.grpc.stub.ServerCallStreamObserver<RideUpdate> serverObserver =
                    (io.grpc.stub.ServerCallStreamObserver<RideUpdate>) responseObserver;
            serverObserver.setOnCancelHandler(() -> {
                synchronized (subscribers) {
                    subscribers.remove(responseObserver);
                }
            });
        }
        // (Note: We don't complete this stream; it remains open for server push)
    }

    // Helper method to convert a Ride JPA entity to the Ride protobuf message
    private com.example.transport.common.proto.Ride convertRideEntityToProto(Ride ride) {
        // use the proto Ride.Builder
        com.example.transport.common.proto.Ride.Builder rideProto =
                com.example.transport.common.proto.Ride.newBuilder();
        rideProto
                .setId(ride.getId())
                .setDestination(ride.getDestination())
                .setDatetime(ride.getDepartureTime().toString())
                .setAvailableSeats(
                        (int) ride.getSeats().stream()
                                .filter(entSeat -> entSeat.getReservation() == null)
                                .count()
                );
        // here entSeat is the JPA entity:
        for (com.example.transport.server.entity.Seat entSeat : ride.getSeats()) {
            String name = entSeat.getReservation() != null
                    ? entSeat.getReservation().getCustomerName()
                    : "";
            // explicitly use the proto.Seat to build:
            com.example.transport.common.proto.Seat protoSeat =
                    com.example.transport.common.proto.Seat.newBuilder()
                            .setNumber(entSeat.getNumber())
                            .setCustomerName(name)
                            .build();
            rideProto.addSeats(protoSeat);
        }
        return rideProto.build();
    }
}