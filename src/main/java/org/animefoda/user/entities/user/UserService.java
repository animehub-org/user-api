package org.animefoda.user.entities.user;

import org.animefoda.user.exception.NotAnEmail;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
    Optional<User> findByEmail(String email)  throws NotAnEmail {
        if(!isValidEmail(email)) {
            throw new NotAnEmail(email);
        }
        return repo.findByEmail(email);
    }
}
