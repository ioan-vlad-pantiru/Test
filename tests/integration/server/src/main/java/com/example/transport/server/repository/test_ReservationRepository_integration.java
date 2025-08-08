import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.transport.server.model.Reservation;
import com.example.transport.server.repository.ReservationRepository;
import com.example.transport.server.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureMockMvc
@Import(ReservationService.class)
public class ReservationRepositoryIntegrationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    private Reservation testReservation;

    @BeforeEach
    public void setUp() {
        testReservation = new Reservation();
        testReservation.setUserId(1L);
        testReservation.setTransportId(1L);
        testReservation.setStartTime(LocalDateTime.now());
        testReservation.setEndTime(LocalDateTime.now().plusHours(2));
    }

    @Test
    @Transactional
    public void testSaveReservation() {
        Reservation savedReservation = reservationRepository.save(testReservation);
        assertThat(savedReservation).isNotNull();
        assertThat(savedReservation.getId()).isGreaterThan(0);
        assertThat(savedReservation.getUserId()).isEqualTo(testReservation.getUserId());
        assertThat(savedReservation.getTransportId()).isEqualTo(testReservation.getTransportId());
    }

    @Test
    @Transactional
    public void testFindReservationById() {
        Reservation savedReservation = reservationRepository.save(testReservation);
        Reservation foundReservation = reservationRepository.findById(savedReservation.getId()).orElse(null);
        assertThat(foundReservation).isNotNull();
        assertThat(foundReservation.getId()).isEqualTo(savedReservation.getId());
    }

    @Test
    @Transactional
    public void testDeleteReservation() {
        Reservation savedReservation = reservationRepository.save(testReservation);
        reservationRepository.delete(savedReservation);
        assertThat(reservationRepository.findById(savedReservation.getId())).isEmpty();
    }

    @Test
    @Transactional
    public void testFindAllReservations() {
        reservationRepository.save(testReservation);
        assertThat(reservationRepository.findAll()).isNotEmpty();
    }

    @Test
    public void testReservationServiceIntegration() throws Exception {
        mockMvc.perform(post("/api/reservations")
                .contentType("application/json")
                .content("{\"userId\":1,\"transportId\":1,\"startTime\":\"2023-10-01T10:00:00\",\"endTime\":\"2023-10-01T12:00:00\"}"))
                .andExpect(status().isCreated());
    }
}