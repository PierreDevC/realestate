package com.estatehub.estate_hub_backend.enums;

public enum PropertyType {
    APARTMENT("Apartment"),
    HOUSE("House"),
    CONDO("Condo"),
    TOWNHOUSE("Townhouse"),
    VILLA("Villa"),
    OFFICE("Office"),
    OTHER("Other");

    private final String displayName;

    PropertyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
