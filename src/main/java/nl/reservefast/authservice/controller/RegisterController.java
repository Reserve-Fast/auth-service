package nl.reservefast.authservice.controller;

import nl.reservefast.authservice.controller.enums.AuthResponse;
import nl.reservefast.authservice.entity.User;
import nl.reservefast.authservice.helper.PasswordHelper;
import nl.reservefast.authservice.jwt.TokenProvider;
import nl.reservefast.authservice.models.RegisterModel;
import nl.reservefast.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/auth")
public class RegisterController {
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final PasswordHelper passwordHelper;

    @Autowired
    public RegisterController(TokenProvider tokenProvider, UserService userService, PasswordHelper passwordHelper) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.passwordHelper = passwordHelper;
    }

    @PostMapping("/register")
    public ResponseEntity login(@Valid @RequestBody RegisterModel registerModel) {
        if(userService.findByEmail(registerModel.getEmail()).isPresent()) {
            return new ResponseEntity<>(AuthResponse.USER_ALREADY_EXISTS.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            User user = new User();

            user.setEmail(registerModel.getEmail());
            user.setFirstName(registerModel.getFirstName());
            user.setLastName(registerModel.getLastName());
            user.setPassword(passwordHelper.hash(registerModel.getPassword()));

            User createdUser = userService.createOrUpdate(user);

            Map<Object, Object> model = new LinkedHashMap<>();
            model.put("token", tokenProvider.createToken(createdUser.getId(), createdUser.getFirstName(), createdUser.getLastName(), createdUser.getRole()));
            model.put("user", createdUser);
            return ok(model);
        } catch(Exception ex) {
            return new ResponseEntity<>(AuthResponse.UNEXPECTED_ERROR.toString(), HttpStatus.BAD_REQUEST);
        }
    }
}