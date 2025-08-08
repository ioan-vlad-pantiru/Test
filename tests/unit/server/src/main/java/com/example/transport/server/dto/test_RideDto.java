import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.transport.server.dto.RideDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RideDtoTest {

    private RideDto rideDto;

    @BeforeEach
    public void setUp() {
        rideDto = new RideDto();
    }

    @Test
    public void testGetAndSetId() {
        Long expectedId = 1L;
        rideDto.setId(expectedId);
        assertEquals(expectedId, rideDto.getId(), "The ID should match the expected value.");
    }

    @Test
    public void testGetAndSetDriverId() {
        Long expectedDriverId = 2L;
        rideDto.setDriverId(expectedDriverId);
        assertEquals(expectedDriverId, rideDto.getDriverId(), "The Driver ID should match the expected value.");
    }

    @Test
    public void testGetAndSetPassengerId() {
        Long expectedPassengerId = 3L;
        rideDto.setPassengerId(expectedPassengerId);
        assertEquals(expectedPassengerId, rideDto.getPassengerId(), "The Passenger ID should match the expected value.");
    }

    @Test
    public void testGetAndSetPickupLocation() {
        String expectedPickupLocation = "123 Main St";
        rideDto.setPickupLocation(expectedPickupLocation);
        assertEquals(expectedPickupLocation, rideDto.getPickupLocation(), "The Pickup Location should match the expected value.");
    }

    @Test
    public void testGetAndSetDropoffLocation() {
        String expectedDropoffLocation = "456 Elm St";
        rideDto.setDropoffLocation(expectedDropoffLocation);
        assertEquals(expectedDropoffLocation, rideDto.getDropoffLocation(), "The Dropoff Location should match the expected value.");
    }

    @Test
    public void testGetAndSetRideStatus() {
        String expectedRideStatus = "COMPLETED";
        rideDto.setRideStatus(expectedRideStatus);
        assertEquals(expectedRideStatus, rideDto.getRideStatus(), "The Ride Status should match the expected value.");
    }

    @Test
    public void testGetAndSetFare() {
        Double expectedFare = 15.75;
        rideDto.setFare(expectedFare);
        assertEquals(expectedFare, rideDto.getFare(), "The Fare should match the expected value.");
    }

    @Test
    public void testToString() {
        rideDto.setId(1L);
        rideDto.setDriverId(2L);
        rideDto.setPassengerId(3L);
        rideDto.setPickupLocation("123 Main St");
        rideDto.setDropoffLocation("456 Elm St");
        rideDto.setRideStatus("COMPLETED");
        rideDto.setFare(15.75);

        String expectedString = "RideDto{id=1, driverId=2, passengerId=3, pickupLocation='123 Main St', dropoffLocation='456 Elm St', rideStatus='COMPLETED', fare=15.75}";
        assertEquals(expectedString, rideDto.toString(), "The toString method should return the expected string representation.");
    }
}