package com.trakto.traktoroute.routes.domain.exceptions;

public class RoutingUnavailableException extends RuntimeException {
    public RoutingUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}