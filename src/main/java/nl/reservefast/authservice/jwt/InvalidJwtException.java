package nl.reservefast.authservice.jwt;

import org.springframework.security.core.AuthenticationException;

public class InvalidJwtException extends AuthenticationException {
    InvalidJwtException(String e) {
        super(e);
    }
}