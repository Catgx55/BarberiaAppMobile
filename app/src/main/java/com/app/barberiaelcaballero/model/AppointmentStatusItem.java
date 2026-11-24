package com.app.barberiaelcaballero.model;

public class AppointmentStatusItem {
    private Long id;
    private String name;

    public Long getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name != null ? name : ("Estado #" + id);
    }
}
