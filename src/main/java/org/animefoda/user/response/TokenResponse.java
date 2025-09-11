package org.animefoda.user.response;

import java.io.Serializable;

public record TokenResponse(
    String accessToken,
    String refreshToken,
    Long expiresAt
) implements Serializable {
}
