package com.app.barberiaelcaballero;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.app.barberiaelcaballero.model.AppointmentResponse;
import com.app.barberiaelcaballero.model.BarberItem;
import com.app.barberiaelcaballero.model.PublicAppointmentRequest;
import com.app.barberiaelcaballero.model.ServiceItem;
import com.app.barberiaelcaballero.network.ApiClient;
import com.app.barberiaelcaballero.network.BarberiaApiService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendarCitaActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPhone, etFecha, etHora, etNotas;
    private Spinner spBarbero, spServicio, spPago;
    private Button btnConfirmarCita;
    private TextView tvEstadoCita;

    private List<BarberItem> listaBarberos = new ArrayList<>();
    private List<ServiceItem> listaServicios = new ArrayList<>();
    private BarberiaApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etNotas = findViewById(R.id.etNotas);
        spBarbero = findViewById(R.id.spBarbero);
        spServicio = findViewById(R.id.spServicio);
        spPago = findViewById(R.id.spPago);
        btnConfirmarCita = findViewById(R.id.btnConfirmarCita);
        tvEstadoCita = findViewById(R.id.tvEstadoCita);

        apiService = ApiClient.getClient().create(BarberiaApiService.class);

        String[] metodosPago = {"EFECTIVO", "NEQUI", "DAVIPLATA"};
        spPago.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, metodosPago));

        etFecha.setOnClickListener(v -> mostrarSelectorFecha());
        etHora.setOnClickListener(v -> mostrarSelectorHora());

        cargarCatalogos();
        btnConfirmarCita.setOnClickListener(v -> enviarCita());
    }

    private void mostrarSelectorFecha() {
        Calendar hoy = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fechaFormateada = String.format(Locale.US, "%04d-%02d-%02d",
                            year, month + 1, dayOfMonth);
                    etFecha.setText(fechaFormateada);
                },
                hoy.get(Calendar.YEAR),
                hoy.get(Calendar.MONTH),
                hoy.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMinDate(hoy.getTimeInMillis());
        dialog.show();
    }

    private void mostrarSelectorHora() {
        Calendar ahora = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String horaFormateada = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
                    etHora.setText(horaFormateada);
                },
                ahora.get(Calendar.HOUR_OF_DAY),
                0,
                true
        );
        dialog.show();
    }

    private void cargarCatalogos() {
        apiService.getBarbers().enqueue(new Callback<List<BarberItem>>() {
            @Override
            public void onResponse(Call<List<BarberItem>> call, Response<List<BarberItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaBarberos = response.body();
                    List<String> nombres = new ArrayList<>();
                    if (listaBarberos.isEmpty()) {
                        nombres.add("Sin barberos disponibles");
                        tvEstadoCita.setText("Aviso: No hay barberos activos registrados aún.");
                    } else {
                        for (BarberItem b : listaBarberos) {
                            nombres.add(b.getName());
                        }
                    }
                    spBarbero.setAdapter(new ArrayAdapter<>(AgendarCitaActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, nombres));
                }
            }

            @Override
            public void onFailure(Call<List<BarberItem>> call, Throwable t) {
                spBarbero.setAdapter(new ArrayAdapter<>(AgendarCitaActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Collections.singletonList("Error al cargar barberos")));
            }
        });

        apiService.getServices().enqueue(new Callback<List<ServiceItem>>() {
            @Override
            public void onResponse(Call<List<ServiceItem>> call, Response<List<ServiceItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaServicios = response.body();
                    List<String> nombres = new ArrayList<>();
                    if (listaServicios.isEmpty()) {
                        nombres.add("Sin servicios disponibles");
                    } else {
                        for (ServiceItem s : listaServicios) {
                            nombres.add(s.getName() + " ($" + s.getPrice() + ")");
                        }
                    }
                    spServicio.setAdapter(new ArrayAdapter<>(AgendarCitaActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, nombres));
                }
            }

            @Override
            public void onFailure(Call<List<ServiceItem>> call, Throwable t) {
                spServicio.setAdapter(new ArrayAdapter<>(AgendarCitaActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Collections.singletonList("Error al cargar servicios")));
            }
        });
    }

    private void enviarCita() {
        if (listaBarberos.isEmpty() || listaServicios.isEmpty()) {
            tvEstadoCita.setText("Debes tener al menos un barbero y un servicio activos para agendar.");
            return;
        }

        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || phone.isEmpty() || fecha.isEmpty() || hora.isEmpty()) {
            tvEstadoCita.setText("Completa nombre, apellido, correo, teléfono, fecha y hora.");
            return;
        }

        Long barberId = listaBarberos.get(spBarbero.getSelectedItemPosition()).getId();
        Long serviceId = listaServicios.get(spServicio.getSelectedItemPosition()).getId();

        PublicAppointmentRequest request = new PublicAppointmentRequest(
                firstName,
                lastName,
                email,
                phone,
                barberId,
                fecha,
                hora,
                etNotas.getText().toString().trim(),
                Collections.singletonList(serviceId),
                spPago.getSelectedItem().toString()
        );

        btnConfirmarCita.setEnabled(false);
        tvEstadoCita.setText("Agendando cita...");

        apiService.createAppointment(request).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                btnConfirmarCita.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Long idCita = response.body().getId();
                    limpiarFormulario();
                    new AlertDialog.Builder(AgendarCitaActivity.this)
                            .setTitle("Cita confirmada")
                            .setMessage("Tu reserva #" + idCita + " fue registrada con éxito.")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", (dialog, which) -> finish())
                            .show();
                } else {
                    tvEstadoCita.setText("No se pudo agendar (código " + response.code()
                            + "). Verifica horario disponible o formato de correo/teléfono.");
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                btnConfirmarCita.setEnabled(true);
                tvEstadoCita.setText("Error de red: " + t.getMessage());
            }
        });
    }

    private void limpiarFormulario() {
        etFirstName.setText("");
        etLastName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etFecha.setText("");
        etHora.setText("");
        etNotas.setText("");
        tvEstadoCita.setText("");
    }
}