package com.estatehub.estate_hub_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Table(name = "locations")
public class Location {
    // Properties
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address", nullable = false)
    @NotBlank(message = "Address is required")
    @Size(min = 3, max = 255, message = "Address must be between 3 and 255 characters")
    private String address;

    @Column(name = "city", nullable = false)
    @NotBlank(message = "City is required")
    @Size(min = 3, max = 100, message = "City must be between 3 and 100 characters")
    private String city;

    @Column(name = "state", nullable = false)
    @NotBlank(message = "State is required")
    @Size(min = 3, max = 100, message = "State must be between 3 and 100 characters")
    private String state;

    @Column(name = "country", nullable = false)
    @NotBlank(message = "Country is required")
    @Size(min = 3, max = 100, message = "Country must be between 3 and 100 characters")
    private String country;

    @Column(name = "postal_code", nullable = false)
    @NotBlank(message = "Postal code is required")
    @Size(min = 3, max = 20, message = "Postal code must be between 3 and 20 characters")
    private String postalCode;

    @Column(name = "coordinates", columnDefinition = "POINT")
    @NotNull(message = "Coordinates are required")
    private Point coordinates;

    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Location() {} 

    public Location(String address, String city, String state, String country, 
                    String postalCode, Point coordinates) {
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.coordinates = coordinates;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public Point getCoordinates() { return coordinates; }
    public void setCoordinates(Point coordinates) { this.coordinates = coordinates; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }    
}
