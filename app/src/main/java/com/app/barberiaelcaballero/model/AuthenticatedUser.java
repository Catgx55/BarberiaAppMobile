package com.app.barberiaelcaballero.model;

import java.util.List;

public class AuthenticatedUser {
    private String email;
    private List<String> roles;

    public String getEmail() { return email; }
    public List<String> getRoles() { return roles; }
}
