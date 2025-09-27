package org.animefoda.user.request.login;

import java.io.Serializable;

public record LoginEncrypted(
    String encryptedInfo,
    String recaptchaToken
) implements Serializable {
}
