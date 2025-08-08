import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.transport.server.repository.UserRepository;
import com.example.transport.server.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);
    }

    @Test
    public void whenFindByUsername_thenReturnUser() {
        Optional<User> foundUser = userRepository.findByUsername(testUser.getUsername());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void whenSaveUser_thenUserIsPersisted() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setEmail("newuser@example.com");

        userRepository.save(newUser);
        Optional<User> foundUser = userRepository.findByUsername(newUser.getUsername());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(newUser.getEmail());
    }

    @Test
    public void whenDeleteUser_thenUserIsRemoved() {
        userRepository.delete(testUser);
        Optional<User> foundUser = userRepository.findByUsername(testUser.getUsername());
        assertThat(foundUser).isNotPresent();
    }

    @Test
    public void whenUserWithDuplicateUsername_thenThrowException() {
        User duplicateUser = new User();
        duplicateUser.setUsername(testUser.getUsername());
        duplicateUser.setPassword("anotherpassword");
        duplicateUser.setEmail("duplicate@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(duplicateUser);
        });
    }

    @Test
    public void whenFindByEmail_thenReturnUser() {
        Optional<User> foundUser = userRepository.findByEmail(testUser.getEmail());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(testUser.getUsername());
    }
}