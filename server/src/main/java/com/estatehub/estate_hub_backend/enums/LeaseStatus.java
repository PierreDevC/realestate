package com.estatehub.estate_hub_backend.enums;

public enum LeaseStatus {
    ACTIVE("Active"),
    EXPIRED("Expired"), 
    TERMINATED("Terminated"),
    RENEWED("Renewed");
    
    private final String displayName;
    
    LeaseStatus(String displayName) {
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