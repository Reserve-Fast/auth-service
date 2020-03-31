package nl.reservefast.authservice.service;

import nl.reservefast.authservice.entity.User;
import nl.reservefast.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return this.userRepository.findUserByEmail(email);
    }

    public User createOrUpdate(User user) {
        return userRepository.save(user);
    }
}

