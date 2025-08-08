import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.transport.server.repository.SeatRepository;
import com.example.transport.server.model.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@DataJpaTest
@AutoConfigureMockMvc
@Import(SeatRepository.class)
class SeatRepositoryIntegrationTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        seatRepository.deleteAll();
    }

    @Test
    void testSaveSeat() {
        // Arrange
        Seat seat = new Seat();
        seat.setSeatNumber("A1");
        seat.setAvailable(true);

        // Act
        Seat savedSeat = seatRepository.save(seat);

        // Assert
        assertThat(savedSeat).isNotNull();
        assertThat(savedSeat.getId()).isGreaterThan(0);
        assertThat(savedSeat.getSeatNumber()).isEqualTo("A1");
        assertThat(savedSeat.isAvailable()).isTrue();
    }

    @Test
    void testFindSeatById() {
        // Arrange
        Seat seat = new Seat();
        seat.setSeatNumber("A2");
        seat.setAvailable(true);
        seat = seatRepository.save(seat);

        // Act
        Optional<Seat> foundSeat = seatRepository.findById(seat.getId());

        // Assert
        assertThat(foundSeat).isPresent();
        assertThat(foundSeat.get().getSeatNumber()).isEqualTo("A2");
    }

    @Test
    void testDeleteSeat() {
        // Arrange
        Seat seat = new Seat();
        seat.setSeatNumber("A3");
        seat.setAvailable(true);
        seat = seatRepository.save(seat);

        // Act
        seatRepository.delete(seat);
        Optional<Seat> foundSeat = seatRepository.findById(seat.getId());

        // Assert
        assertThat(foundSeat).isNotPresent();
    }

    @Test
    void testSeatAvailability() {
        // Arrange
        Seat seat = new Seat();
        seat.setSeatNumber("A4");
        seat.setAvailable(false);
        seatRepository.save(seat);

        // Act
        Optional<Seat> foundSeat = seatRepository.findById(seat.getId());

        // Assert
        assertThat(foundSeat).isPresent();
        assertThat(foundSeat.get().isAvailable()).isFalse();
    }

    @Test
    void testSeatNotFound() {
        // Act
        Optional<Seat> foundSeat = seatRepository.findById(999L); // Non-existing ID

        // Assert
        assertThat(foundSeat).isNotPresent();
    }
}