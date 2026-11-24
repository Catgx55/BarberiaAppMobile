package com.app.barberiaelcaballero;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.barberiaelcaballero.model.AppointmentResponse;
import com.app.barberiaelcaballero.model.AppointmentStatusItem;
import com.app.barberiaelcaballero.model.AppointmentStatusUpdateRequest;
import com.app.barberiaelcaballero.model.AuthenticatedUser;
import com.app.barberiaelcaballero.network.ApiClient;
import com.app.barberiaelcaballero.network.BarberiaApiService;
import com.app.barberiaelcaballero.ui.AppointmentAdapter;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanelAdminActivity extends AppCompatActivity {

    private LinearLayout layoutLogin, layoutPanel;
    private EditText etLoginEmail, etLoginPassword;
    private Button btnIniciarSesion, btnRecargarCitas, btnCerrarSesion;
    private TextView tvUsuarioLogueado, tvEstadoPanel;
    private RecyclerView rvCitas;

    private BarberiaApiService apiService;
    private AppointmentAdapter appointmentAdapter;
    private String authHeader = null;
    private List<AppointmentStatusItem> estadosDisponibles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_admin);

        layoutLogin = findViewById(R.id.layoutLogin);
        layoutPanel = findViewById(R.id.layoutPanel);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnRecargarCitas = findViewById(R.id.btnRecargarCitas);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        tvUsuarioLogueado = findViewById(R.id.tvUsuarioLogueado);
        tvEstadoPanel = findViewById(R.id.tvEstadoPanel);
        rvCitas = findViewById(R.id.rvCitas);

        apiService = ApiClient.getClient().create(BarberiaApiService.class);

        rvCitas.setLayoutManager(new LinearLayoutManager(this));
        appointmentAdapter = new AppointmentAdapter(this::mostrarDialogoCambiarEstado);
        rvCitas.setAdapter(appointmentAdapter);

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
        btnRecargarCitas.setOnClickListener(v -> cargarCitas());
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void iniciarSesion() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            tvEstadoPanel.setText("Ingresa correo y contraseña.");
            return;
        }

        String headerTentativo = Credentials.basic(email, password);
        btnIniciarSesion.setEnabled(false);
        tvEstadoPanel.setText("Verificando credenciales...");

        apiService.getCurrentUser(headerTentativo).enqueue(new Callback<AuthenticatedUser>() {
            @Override
            public void onResponse(Call<AuthenticatedUser> call, Response<AuthenticatedUser> response) {
                btnIniciarSesion.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    authHeader = headerTentativo;
                    AuthenticatedUser user = response.body();
                    tvUsuarioLogueado.setText("Conectado: " + user.getEmail() + " " + user.getRoles());
                    layoutLogin.setVisibility(View.GONE);
                    layoutPanel.setVisibility(View.VISIBLE);
                    tvEstadoPanel.setText("");
                    cargarEstadosYCitas();
                } else {
                    tvEstadoPanel.setText("Credenciales incorrectas (código " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<AuthenticatedUser> call, Throwable t) {
                btnIniciarSesion.setEnabled(true);
                tvEstadoPanel.setText("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void cargarEstadosYCitas() {
        apiService.getAppointmentStatuses(authHeader).enqueue(new Callback<List<AppointmentStatusItem>>() {
            @Override
            public void onResponse(Call<List<AppointmentStatusItem>> call, Response<List<AppointmentStatusItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    estadosDisponibles = response.body();
                }
                cargarCitas();
            }

            @Override
            public void onFailure(Call<List<AppointmentStatusItem>> call, Throwable t) {
                cargarCitas();
            }
        });
    }

    private void cargarCitas() {
        tvEstadoPanel.setText("Cargando citas...");
        apiService.getAppointments(authHeader).enqueue(new Callback<List<AppointmentResponse>>() {
            @Override
            public void onResponse(Call<List<AppointmentResponse>> call, Response<List<AppointmentResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AppointmentResponse> citas = response.body();
                    appointmentAdapter.setCitas(citas);
                    tvEstadoPanel.setText(citas.isEmpty() ? "No hay citas registradas." : "Total citas: " + citas.size());
                } else {
                    tvEstadoPanel.setText("No se pudieron cargar las citas (código " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentResponse>> call, Throwable t) {
                tvEstadoPanel.setText("Error de red: " + t.getMessage());
            }
        });
    }

    private void mostrarDialogoCambiarEstado(AppointmentResponse cita) {
        if (estadosDisponibles.isEmpty()) {
            Toast.makeText(this, "No se han cargado los estados disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nombresEstados = new String[estadosDisponibles.size()];
        for (int i = 0; i < estadosDisponibles.size(); i++) {
            nombresEstados[i] = estadosDisponibles.get(i).getName();
        }

        new AlertDialog.Builder(this)
                .setTitle("Estado para cita #" + cita.getId())
                .setItems(nombresEstados, (dialog, which) -> {
                    Long nuevoStatusId = estadosDisponibles.get(which).getId();
                    actualizarEstadoCita(cita.getId(), nuevoStatusId);
                })
                .show();
    }

    private void actualizarEstadoCita(Long appointmentId, Long statusId) {
        tvEstadoPanel.setText("Actualizando estado...");
        apiService.updateAppointmentStatus(authHeader, appointmentId, new AppointmentStatusUpdateRequest(statusId))
                .enqueue(new Callback<AppointmentResponse>() {
                    @Override
                    public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(PanelAdminActivity.this, "Estado actualizado", Toast.LENGTH_SHORT).show();
                            cargarCitas();
                        } else {
                            tvEstadoPanel.setText("Error al actualizar (código " + response.code() + ").");
                        }
                    }

                    @Override
                    public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                        tvEstadoPanel.setText("Error de red: " + t.getMessage());
                    }
                });
    }

    private void cerrarSesion() {
        authHeader = null;
        etLoginPassword.setText("");
        appointmentAdapter.setCitas(new ArrayList<>());
        layoutPanel.setVisibility(View.GONE);
        layoutLogin.setVisibility(View.VISIBLE);
        tvEstadoPanel.setText("Sesión cerrada.");
    }
}
