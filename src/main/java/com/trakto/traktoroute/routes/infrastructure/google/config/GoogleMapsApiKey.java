package com.trakto.traktoroute.routes.infrastructure.google.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleMapsApiKey   {
    private final String value;

    GoogleMapsApiKey(@Value("${maps.api.key}")String value){
        if (value == null || value.isBlank()){
            throw new IllegalStateException("MAPS_API_KEY not set. Set env var MAPS_API_KEY before running.");
        }
        this.value = value;
    }
    public String value(){
        return value;
    }
}
