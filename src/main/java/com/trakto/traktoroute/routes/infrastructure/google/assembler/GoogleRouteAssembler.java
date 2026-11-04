package com.trakto.traktoroute.routes.infrastructure.google.assembler;

import com.google.maps.routing.v2.*;
import com.google.type.LatLng;
import com.trakto.traktoroute.routes.domain.model.valueobjects.GeoPoint;
import com.trakto.traktoroute.routes.domain.model.valueobjects.RouteResult;

import java.time.Instant;

public final class GoogleRouteAssembler {

    private GoogleRouteAssembler() {}


    public static ComputeRoutesRequest toGoogleFromDomain(GeoPoint origin,
                                                          GeoPoint destination) {
        return ComputeRoutesRequest.newBuilder()
                .setOrigin(toWaypoint(origin))
                .setDestination(toWaypoint(destination))
                .setTravelMode(RouteTravelMode.DRIVE)
                .setRoutingPreference(RoutingPreference.TRAFFIC_AWARE)
                .setPolylineEncoding(PolylineEncoding.ENCODED_POLYLINE)
                .setUnits(Units.METRIC)
                .build();
    }
    // Google -> Domain
    public static RouteResult toDomainFromGoogle(Route route, Instant calculatedAt) {
        return new RouteResult(
                route.getDistanceMeters(),
                route.getDuration().getSeconds(),
                route.getPolyline().getEncodedPolyline(),
                calculatedAt
        );
    }

    private static Waypoint toWaypoint(GeoPoint point) {
        return Waypoint.newBuilder()
                .setLocation(Location.newBuilder()
                        .setLatLng(LatLng.newBuilder()
                                .setLatitude(point.latitude())
                                .setLongitude(point.longitude())))
                .build();
    }
}
