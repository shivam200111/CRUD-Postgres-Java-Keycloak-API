package com.shivam.keycloak;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table(name = "Employee3")  // Updated to match the new table name "Employee3"
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "keycloak_user_id", unique = true)
    private String keycloakUserId;  // New field for Keycloak user ID

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String address;

    @NotNull
    private String password;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;  // New field for created timestamp

    @Column(name = "updated_at")
    private Timestamp updatedAt;  // New field for updated timestamp



    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeycloakUserId() {
        return keycloakUserId;
    }

    public void setKeycloakUserId(String keycloakUserId) {
        this.keycloakUserId = keycloakUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
