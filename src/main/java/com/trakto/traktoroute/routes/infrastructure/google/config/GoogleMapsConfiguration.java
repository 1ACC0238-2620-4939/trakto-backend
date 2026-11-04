package com.trakto.traktoroute.routes.infrastructure.google.config;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.rpc.FixedHeaderProvider;
import com.google.maps.routing.v2.RoutesClient;
import com.google.maps.routing.v2.RoutesSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Configuration
public class GoogleMapsConfiguration {

    private static final String ROUTES_FIELD_MASK =
            "routes.distanceMeters,routes.duration,routes.polyline.encodedPolyline";

    @Bean
    RoutesClient routesClient(GoogleMapsApiKey apiKey) throws IOException {
        RoutesSettings.Builder builder = RoutesSettings.newBuilder()
                .setHeaderProvider(FixedHeaderProvider.create(Map.of(
                        "x-goog-api-key", apiKey.value(),
                        "x-goog-fieldmask", ROUTES_FIELD_MASK)))
                .setCredentialsProvider(NoCredentialsProvider.create());

        builder.computeRoutesSettings()
                .setRetrySettings(builder.computeRoutesSettings().getRetrySettings().toBuilder()
                        .setInitialRpcTimeoutDuration(Duration.ofSeconds(2))
                        .setMaxRpcTimeoutDuration(Duration.ofSeconds(2))
                        .setTotalTimeoutDuration(Duration.ofSeconds(5))
                        .setMaxAttempts(2)
                        .build()
                );
        return RoutesClient.create(builder.build());
    }
}
