package org.animefoda.user.request;

import java.util.Date;

public record RegisterRequest(
        String name,
        String surname,
        String username,
        String email,
        String password,
        Date birthDate) {
}
