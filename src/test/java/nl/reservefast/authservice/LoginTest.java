package nl.reservefast.authservice;

import nl.reservefast.authservice.controller.LoginController;
import nl.reservefast.authservice.controller.RegisterController;
import nl.reservefast.authservice.entity.User;
import nl.reservefast.authservice.entity.enums.Role;
import nl.reservefast.authservice.helper.PasswordHelper;
import nl.reservefast.authservice.jwt.TokenProvider;
import nl.reservefast.authservice.models.AuthorisationModel;
import nl.reservefast.authservice.models.RegisterModel;
import nl.reservefast.authservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class LoginTest {

    @InjectMocks
    private LoginController loginController;

    @InjectMocks
    private RegisterController registerController;

    @Mock
    private UserService userService;

    @Mock
    private PasswordHelper passwordHelper;

    @Mock
    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        UUID accountId = UUID.fromString("123e4567-e89b-42d3-a456-556642440010");

        User user = new User();
        user.setEmail("info@rensmanders.nl");
        user.setFirstName("Rens");
        user.setLastName("Manders");
        user.setRole(Role.USER);
        user.setPassword("lol123");
        user.setId(accountId);

        lenient().when(tokenProvider.createToken(user.getId(), user.getFirstName(), user.getLastName(), user.getRole())).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiNTdmOWZlMC1kYTU4LTQ4MzQtOWQ2YS0wZGIyY2I3OWE0MTciLCJ1c2VybmFtZSI6IlJlbnMgTWFuZGVycyIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNTkyNjU5NDA4LCJleHAiOjE1OTI3NDU4MDh9.nJScZ7Rm1HtWNMBKGcovwO3B931QpOXGk-wtDamrGcM");
        lenient().when(passwordHelper.isMatch("lol123", "lol123")).thenReturn(true);
        lenient().when(userService.findByEmail("info@rensmanders.nl")).thenReturn(Optional.of(user));
    }

    @Test
    void login() {
        AuthorisationModel model = new AuthorisationModel();
        model.setEmail("info@rensmanders.nl");
        model.setPassword("lol123");

        HttpStatus status = loginController.login(model).getStatusCode();
        assertEquals(200, status.value());
    }

    @Test
    void incorrectLogin() {
        AuthorisationModel model = new AuthorisationModel();
        model.setEmail("info@rend.nl");
        model.setPassword("lol123");

        HttpStatus status = loginController.login(model).getStatusCode();
        assertEquals(400, status.value());
    }
}
