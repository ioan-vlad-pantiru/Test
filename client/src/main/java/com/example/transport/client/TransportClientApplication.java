package com.example.transport.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import com.example.transport.common.proto.*;

public class TransportClientApplication extends Application {
    private ManagedChannel channel;
    private TransportServiceGrpc.TransportServiceBlockingStub blockingStub;
    private TransportServiceGrpc.TransportServiceStub asyncStub;
    private String loggedInUser;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        // Initialize gRPC channel and stubs
        channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
        blockingStub = TransportServiceGrpc.newBlockingStub(channel);
        asyncStub = TransportServiceGrpc.newStub(channel);
        // Show login window first
        showLoginWindow();
    }

    private void showLoginWindow() {
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        Label userLabel = new Label("Username:");
        Label passLabel = new Label("Password:");
        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Login");
        Label statusLabel = new Label();
        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(statusLabel, 1, 3);

        loginButton.setOnAction(e -> {
            String user = userField.getText().trim();
            String pass = passField.getText().trim();
            LoginRequest request = LoginRequest.newBuilder()
                    .setUsername(user).setPassword(pass).build();
            LoginResponse response = blockingStub.login(request);
            if (response.getSuccess()) {
                loggedInUser = user;
                loginStage.close();
                showMainWindow();
            } else {
                statusLabel.setText("Login failed: " + response.getMessage());
            }
        });

        Scene scene = new Scene(grid, 300, 150);
        loginStage.setScene(scene);
        loginStage.show();
    }

    private void showMainWindow() {
        primaryStage.setTitle("Transport Reservation System");
        BorderPane root = new BorderPane();
        // Top bar with search fields and logout button
        HBox topBar = new HBox(10);
        TextField destinationField = new TextField();
        destinationField.setPromptText("Destination");
        TextField dateField = new TextField();
        dateField.setPromptText("YYYY-MM-DD");
        Button searchButton = new Button("Search");
        Button logoutButton = new Button("Logout");
        topBar.getChildren().addAll(new Label("Destination:"), destinationField,
                new Label("Date:"), dateField, searchButton, logoutButton);
        // Left side: list of rides
        ListView<RideInfo> ridesListView = new ListView<>();
        // Right side: ride details and reservation controls
        VBox rightPane = new VBox(10);
        Label rideDetailsLabel = new Label("Ride Details:");
        ListView<SeatInfo> seatsListView = new ListView<>();
        HBox reservePane = new HBox(5);
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        Spinner<Integer> seatsSpinner = new Spinner<>(1, 10, 1);
        Button reserveButton = new Button("Reserve");
        reservePane.getChildren().addAll(new Label("Name:"), nameField,
                new Label("Seats:"), seatsSpinner, reserveButton);
        rightPane.getChildren().addAll(rideDetailsLabel, seatsListView, reservePane);

        root.setTop(topBar);
        root.setLeft(ridesListView);
        root.setCenter(rightPane);
        Scene scene = new Scene(root, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load all rides initially
        ListRidesResponse allRides = blockingStub.listRides(com.google.protobuf.Empty.getDefaultInstance());
        for (com.example.transport.common.proto.Ride rideProto : allRides.getRidesList()) {
            ridesListView.getItems().add(new RideInfo(rideProto));
        }

        // Update seat list when a ride is selected from the list
        ridesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateRideDetails(newVal, rideDetailsLabel, seatsListView);
            }
        });

        // Search button: filter rides
        searchButton.setOnAction(e -> {
            String destFilter = destinationField.getText().trim();
            String dateFilter = dateField.getText().trim();
            SearchRequest req = SearchRequest.newBuilder()
                    .setDestination(destFilter != null ? destFilter : "")
                    .setDate(dateFilter != null ? dateFilter : "")
                    .build();
            ListRidesResponse results = blockingStub.searchRides(req);
            ridesListView.getItems().clear();
            for (com.example.transport.common.proto.Ride rideProto : results.getRidesList()) {
                ridesListView.getItems().add(new RideInfo(rideProto));
            }
        });

        // Reserve button: attempt to reserve selected seats
        reserveButton.setOnAction(e -> {
            RideInfo selected = ridesListView.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            String customerName = nameField.getText().trim();
            int count = seatsSpinner.getValue();
            ReserveRequest req = ReserveRequest.newBuilder()
                    .setRideId(selected.id)
                    .setCustomerName(customerName)
                    .setSeatCount(count)
                    .build();
            ReserveResponse res = blockingStub.reserveSeat(req);
            if (!res.getSuccess()) {
                // Show error message (e.g., not enough seats available)
                Alert alert = new Alert(Alert.AlertType.ERROR, res.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
            // If success, the update will arrive via the stream subscription
        });

        // Logout button: close the app (for simplicity)
        logoutButton.setOnAction(e -> {
            primaryStage.close();
            Platform.exit();
        });

        // Subscribe for real-time ride updates from server
        asyncStub.subscribeRideUpdates(com.google.protobuf.Empty.getDefaultInstance(),
                new StreamObserver<RideUpdate>() {
                    @Override
                    public void onNext(RideUpdate update) {
                        Platform.runLater(() -> {
                            com.example.transport.common.proto.Ride updatedRideProto = update.getRide();
                            // Find the corresponding RideInfo in the list and update it
                            for (RideInfo info : ridesListView.getItems()) {
                                if (info.id == updatedRideProto.getId()) {
                                    info.updateFromProto(updatedRideProto);
                                    // If this ride is currently selected, refresh details view
                                    if (ridesListView.getSelectionModel().getSelectedItem() == info) {
                                        updateRideDetails(info, rideDetailsLabel, seatsListView);
                                    }
                                    break;
                                }
                            }
                        });
                    }
                    @Override
                    public void onError(Throwable t) {
                        System.err.println("Error in update stream: " + t.getMessage());
                    }
                    @Override
                    public void onCompleted() {
                        // Server has closed the update stream
                    }
                });
    }

    private void updateRideDetails(RideInfo rideInfo, Label rideDetailsLabel, ListView<SeatInfo> seatsListView) {
        rideDetailsLabel.setText(rideInfo.destination + " on " + rideInfo.datetime
                + " - " + rideInfo.availableSeats + " seats available");
        seatsListView.getItems().setAll(rideInfo.seats);
    }

    @Override
    public void stop() {
        // Clean up gRPC channel on application exit
        if (channel != null) {
            channel.shutdownNow();
        }
    }

    // Inner classes for holding ride and seat data in the UI
    public static class RideInfo {
        long id;
        String destination;
        String datetime;
        int availableSeats;
        java.util.List<SeatInfo> seats = new java.util.ArrayList<>();
        RideInfo(com.example.transport.common.proto.Ride rideProto) {
            updateFromProto(rideProto);
        }
        void updateFromProto(com.example.transport.common.proto.Ride rideProto) {
            this.id = rideProto.getId();
            this.destination = rideProto.getDestination();
            this.datetime = rideProto.getDatetime();
            this.availableSeats = rideProto.getAvailableSeats();
            seats.clear();
            for (Seat seatProto : rideProto.getSeatsList()) {
                seats.add(new SeatInfo(seatProto.getNumber(), seatProto.getCustomerName()));
            }
        }
        @Override
        public String toString() {
            return destination + " - " + datetime;
        }
    }
    public static class SeatInfo {
        int number;
        String name;
        SeatInfo(int number, String name) {
            this.number = number;
            this.name = (name == null || name.isEmpty()) ? "-" : name;
        }
        @Override
        public String toString() {
            return number + " : " + name;
        }
    }
}