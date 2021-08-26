package ua.yelisieiev.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class TokenWithTTL {
    private final String token;
    private final LocalDateTime validUntil;

    public TokenWithTTL(String token, LocalDateTime validUntil) {
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
