package com.app.barberiaelcaballero.model;

public class AppointmentResponse {
    private Long id;
    private String appointmentDate;
    private String startTime;
    private String endTime;
    private Double totalAmount;
    private Long statusId;
    private String statusName;
    private String clientName;
    private String clientPhone;
    private String barberName;
    private String paymentMethod;
    private String notes;

    public Long getId() { return id; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public Double getTotalAmount() { return totalAmount; }
    public Long getStatusId() { return statusId; }
    public String getStatusName() { return statusName; }
    public String getClientName() { return clientName; }
    public String getClientPhone() { return clientPhone; }
    public String getBarberName() { return barberName; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getNotes() { return notes; }
}