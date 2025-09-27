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

    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return this.repo.findByUsername(username);
    }
}
