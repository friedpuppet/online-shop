package ua.yelisieiev.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class AuthTokenWithTTL {
    private final String token;
    private final LocalDateTime validUntil;

    public AuthTokenWithTTL(String token, LocalDateTime validUntil) {
        this.token = token;
        this.validUntil = validUntil;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }
}
