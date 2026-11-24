package com.app.barberiaelcaballero.network;

import com.app.barberiaelcaballero.model.AppointmentResponse;
import com.app.barberiaelcaballero.model.AppointmentStatusItem;
import com.app.barberiaelcaballero.model.AppointmentStatusUpdateRequest;
import com.app.barberiaelcaballero.model.AuthenticatedUser;
import com.app.barberiaelcaballero.model.BarberItem;
import com.app.barberiaelcaballero.model.PublicAppointmentRequest;
import com.app.barberiaelcaballero.model.ServiceItem;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BarberiaApiService {
    // Ajusta la ruta exacta según tu ServiceController en Spring Boot
    @GET("api/services")
    Call<List<ServiceItem>> getServices();

    // Ajusta la ruta exacta según tu BarberController en Spring Boot
    @GET("api/public/barbers")
    Call<List<BarberItem>> getBarbers();

    @POST("api/public/appointments")
    Call<AppointmentResponse> createAppointment(@Body PublicAppointmentRequest request);

    @GET("api/auth/me")
    Call<AuthenticatedUser> getCurrentUser(@Header("Authorization") String authHeader);

    @GET("api/appointments")
    Call<List<AppointmentResponse>> getAppointments(@Header("Authorization") String authHeader);

    @GET("api/appointment-statuses")
    Call<List<AppointmentStatusItem>> getAppointmentStatuses(@Header("Authorization") String authHeader);

    @PATCH("api/appointments/{id}/status")
    Call<AppointmentResponse> updateAppointmentStatus(
            @Header("Authorization") String authHeader,
            @Path("id") Long appointmentId,
            @Body AppointmentStatusUpdateRequest body
    );
}
