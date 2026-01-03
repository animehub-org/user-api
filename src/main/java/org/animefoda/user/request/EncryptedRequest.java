package org.animefoda.user.request;

import java.io.Serializable;

public record EncryptedRequest(
        String encryptedInfo,
        String recaptchaToken) implements Serializable {
}
