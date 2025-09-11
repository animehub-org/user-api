package org.animefoda.user.exception;

public class NotFound extends BaseError{
    public NotFound(String message) {
        super(message, ErrorCode.NOT_FOUND);
    }
}
