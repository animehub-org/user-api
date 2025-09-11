package org.animefoda.user.exception;

public class NotAnEmail extends BaseError {
    public NotAnEmail(String message) {
        super(message, ErrorCode.NOT_AN_EMAIL);
    }
}
