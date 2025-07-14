package com.example.transport.server;

import com.example.transport.server.grpc.TransportServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TransportServerApplication {
    public static void main(String[] args) throws Exception {
        // Start Spring (which sets up JPA and our TransportServiceImpl bean)
        ConfigurableApplicationContext context =
                SpringApplication.run(TransportServerApplication.class, args);
        // Retrieve the gRPC service bean from Spring
        TransportServiceImpl grpcService = context.getBean(TransportServiceImpl.class);
        // Build and start the gRPC server on port 9090
        Server server = ServerBuilder.forPort(9090)
                .addService(grpcService)
                .build()
                .start();
        System.out.println("gRPC server started on port 8080");
        // Add shutdown hook to stop server gracefully on JVM exit
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        // Keep server running until terminated
        server.awaitTermination();
    }
}