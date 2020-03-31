package nl.reservefast.authservice.controller;

import nl.reservefast.authservice.controller.enums.AuthResponse;
import nl.reservefast.authservice.entity.User;
import nl.reservefast.authservice.helper.PasswordHelper;
import nl.reservefast.authservice.jwt.TokenProvider;
import nl.reservefast.authservice.models.AuthorisationModel;
import nl.reservefast.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/auth")
public class LoginController {
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final PasswordHelper passwordHelper;

    @Autowired
    public LoginController(TokenProvider tokenProvider, UserService userService, PasswordHelper passwordHelper) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.passwordHelper = passwordHelper;
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody AuthorisationModel authModel) {
        Optional<User> user = userService.findByEmail(authModel.getEmail());

        if(!user.isPresent()) {
            return new ResponseEntity<>(AuthResponse.WRONG_CREDENTIALS.toString(), HttpStatus.BAD_REQUEST);
        }

        if(!passwordHelper.isMatch(authModel.getPassword(), user.get().getPassword())) {
            return new ResponseEntity<>(AuthResponse.WRONG_CREDENTIALS.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            Map<Object, Object> model = new LinkedHashMap<>();
            model.put("token", tokenProvider.createToken(user.get().getId(), user.get().getFirstName(), user.get().getLastName(), user.get().getRole()));
            model.put("user", user.get());
            return ok(model);
        } catch(AuthenticationException ex) {
            return new ResponseEntity<>(AuthResponse.UNEXPECTED_ERROR.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user")
    public ResponseEntity currentUser(@AuthenticationPrincipal UserDetails userDetails) {
        Map<Object, Object> model = new LinkedHashMap<>();
        model.put("user", userDetails);
        return ok(model);
    }
}