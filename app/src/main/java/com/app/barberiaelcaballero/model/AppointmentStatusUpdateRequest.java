package com.app.barberiaelcaballero.model;

public class AppointmentStatusUpdateRequest {
    private Long statusId;

    public AppointmentStatusUpdateRequest(Long statusId) {
        this.statusId = statusId;
    }

    public Long getStatusId() { return statusId; }
}
