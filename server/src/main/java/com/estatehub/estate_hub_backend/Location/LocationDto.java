package com.estatehub.estate_hub_backend.Location;

public record LocationDto(
    Long id,
    String address,
    String city,
    String state,
    String country,
    String postalCode,
    Double latitude,
    Double longitude
) {}