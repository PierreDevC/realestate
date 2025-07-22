package com.estatehub.estate_hub_backend.enums;

public enum MaintenancePriority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    EMERGENCY("Emergency");

    private final String displayName;

    MaintenancePriority(String displayName) {
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
