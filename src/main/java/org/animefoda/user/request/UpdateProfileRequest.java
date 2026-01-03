package org.animefoda.user.request;

import java.util.Date;

public record UpdateProfileRequest(
        String name,
        String surname,
        Date birthDate) {
}
