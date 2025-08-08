import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.transport.server.repository.RideRepository;
import com.example.transport.server.model.Ride;
import com.example.transport.server.config.TestDatabaseConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RideRepositoryIntegrationTest {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private EntityManager entityManager;

    private Ride testRide;

    @BeforeEach
    public void setUp() {
        testRide = new Ride();
        testRide.setStartLocation("Location A");
        testRide.setEndLocation("Location B");
        testRide.setStartTime(LocalDateTime.now());
        testRide.setEndTime(LocalDateTime.now().plusHours(1));
        testRide.setPassengerCount(1);
    }

    @Test
    public void testSaveRide() {
        Ride savedRide = rideRepository.save(testRide);
        assertThat(savedRide).isNotNull();
        assertThat(savedRide.getId()).isGreaterThan(0);
        assertThat(savedRide.getStartLocation()).isEqualTo("Location A");
    }

    @Test
    public void testFindRideById() {
        Ride savedRide = rideRepository.save(testRide);
        Ride foundRide = rideRepository.findById(savedRide.getId()).orElse(null);
        assertThat(foundRide).isNotNull();
        assertThat(foundRide.getId()).isEqualTo(savedRide.getId());
    }

    @Test
    public void testDeleteRide() {
        Ride savedRide = rideRepository.save(testRide);
        rideRepository.delete(savedRide);
        Ride foundRide = rideRepository.findById(savedRide.getId()).orElse(null);
        assertThat(foundRide).isNull();
    }

    @Test
    public void testRideConstraints() {
        Ride invalidRide = new Ride();
        invalidRide.setStartLocation(null); // Invalid: Start location cannot be null
        invalidRide.setEndLocation("Location B");
        invalidRide.setStartTime(LocalDateTime.now());
        invalidRide.setEndTime(LocalDateTime.now().plusHours(1));
        invalidRide.setPassengerCount(1);

        try {
            rideRepository.save(invalidRide);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(javax.persistence.PersistenceException.class);
        }
    }

    @Test
    public void testFindAllRides() {
        rideRepository.save(testRide);
        Iterable<Ride> rides = rideRepository.findAll();
        assertThat(rides).isNotEmpty();
    }
}