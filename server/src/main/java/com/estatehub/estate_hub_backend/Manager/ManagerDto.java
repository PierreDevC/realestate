package com.estatehub.estate_hub_backend.Manager;

public record ManagerDto(
    Long id,
    String name,
    String email,
    String phoneNumber
) {}