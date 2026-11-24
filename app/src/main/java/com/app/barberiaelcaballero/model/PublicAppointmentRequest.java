package com.app.barberiaelcaballero.model;

import java.util.List;

public class PublicAppointmentRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Long barberId;
    private String appointmentDate;
    private String startTime;
    private String notes;
    private List<Long> serviceIds;
    private String paymentMethod;

    public PublicAppointmentRequest(String firstName, String lastName, String email, String phone,
                                    Long barberId, String appointmentDate, String startTime,
                                    String notes, List<Long> serviceIds, String paymentMethod) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.barberId = barberId;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.notes = notes;
        this.serviceIds = serviceIds;
        this.paymentMethod = paymentMethod;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Long getBarberId() { return barberId; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getStartTime() { return startTime; }
    public String getNotes() { return notes; }
    public List<Long> getServiceIds() { return serviceIds; }
    public String getPaymentMethod() { return paymentMethod; }
}
