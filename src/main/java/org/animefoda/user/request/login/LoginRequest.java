package org.animefoda.user.request.login;

public record LoginRequest(
    String loginValue,
    String password,
    String fingerprint
) {
    @Override
    public String toString() {
        return "LoginRequest{" +
                "loginValue='" + loginValue + '\'' +
                ", password='" + password + '\'' +
                ", fingerprint='" + fingerprint + '\'' +
                '}';
    }
}
