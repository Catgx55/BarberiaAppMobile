package com.app.barberiaelcaballero;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.barberiaelcaballero.model.BarberItem;
import com.app.barberiaelcaballero.model.ServiceItem;
import com.app.barberiaelcaballero.network.ApiClient;
import com.app.barberiaelcaballero.network.BarberiaApiService;
import com.app.barberiaelcaballero.ui.BarberAdapter;
import com.app.barberiaelcaballero.ui.ServiceAdapter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private TextView tvResultado;
    private Button btnCargarServicios;
    private Button btnCargarBarberos;
    private RecyclerView rvServicios;
    private ServiceAdapter serviceAdapter;
    private BarberAdapter barberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResultado = findViewById(R.id.tvResultado);
        btnCargarServicios = findViewById(R.id.btnCargarServicios);
        btnCargarBarberos = findViewById(R.id.btnCargarBarberos);
        rvServicios = findViewById(R.id.rvServicios);

        rvServicios.setLayoutManager(new LinearLayoutManager(this));
        serviceAdapter = new ServiceAdapter();
        barberAdapter = new BarberAdapter();

        btnCargarServicios.setOnClickListener(v -> cargarServicios());
        btnCargarBarberos.setOnClickListener(v -> cargarBarberos());

        findViewById(R.id.btnIrAgendar).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AgendarCitaActivity.class)));

        findViewById(R.id.btnPanelAdmin).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, PanelAdminActivity.class)));
    }

    private void cargarServicios() {
        tvResultado.setText("Consultando servicios...");

        BarberiaApiService apiService = ApiClient.getClient().create(BarberiaApiService.class);
        Call<List<ServiceItem>> call = apiService.getServices();

        call.enqueue(new Callback<List<ServiceItem>>() {
            @Override
            public void onResponse(Call<List<ServiceItem>> call, Response<List<ServiceItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ServiceItem> servicios = response.body();
                    serviceAdapter.setServicios(servicios);
                    rvServicios.setAdapter(serviceAdapter);
                    if (servicios.isEmpty()) {
                        tvResultado.setText("No hay servicios registrados.");
                        return;
                    }

                    // 2. Pasar la lista de servicios al RecyclerView mediante el Adapter
                    serviceAdapter.setServicios(servicios);
                    tvResultado.setText("Servicios cargados: " + servicios.size());
                } else {
                    tvResultado.setText("Error del servidor: código " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ServiceItem>> call, Throwable t) {
                tvResultado.setText("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void cargarBarberos() {
        tvResultado.setText("Consultando barberos...");

        BarberiaApiService apiService = ApiClient.getClient().create(BarberiaApiService.class);
        Call<List<BarberItem>> call = apiService.getBarbers();

        call.enqueue(new Callback<List<BarberItem>>() {
            @Override
            public void onResponse(Call<List<BarberItem>> call, Response<List<BarberItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BarberItem> barberos = response.body();
                    barberAdapter.setBarberos(barberos);
                    rvServicios.setAdapter(barberAdapter);
                    if (barberos.isEmpty()) {
                        tvResultado.setText("No hay barberos registrados.");
                    } else {
                        tvResultado.setText("Barberos cargados: " + barberos.size());
                    }
                } else {
                    tvResultado.setText("Error del servidor: código " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<BarberItem>> call, Throwable t) {
                tvResultado.setText("Error de conexión: " + t.getMessage());
            }
        });
    }
}