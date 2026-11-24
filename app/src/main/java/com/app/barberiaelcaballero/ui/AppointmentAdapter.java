package com.app.barberiaelcaballero.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.barberiaelcaballero.R;
import com.app.barberiaelcaballero.model.AppointmentResponse;
import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    public interface OnCambiarEstadoClickListener {
        void onCambiarEstadoClick(AppointmentResponse cita);
    }

    private List<AppointmentResponse> citas = new ArrayList<>();
    private final OnCambiarEstadoClickListener listener;

    public AppointmentAdapter(OnCambiarEstadoClickListener listener) {
        this.listener = listener;
    }

    public void setCitas(List<AppointmentResponse> nuevasCitas) {
        this.citas = nuevasCitas != null ? nuevasCitas : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentResponse cita = citas.get(position);

        String cliente = cita.getClientName() != null ? cita.getClientName() : "Cliente";
        holder.tvCitaCliente.setText("#" + cita.getId() + " • " + cliente);

        String detalle = (cita.getAppointmentDate() != null ? cita.getAppointmentDate() : "")
                + " " + (cita.getStartTime() != null ? cita.getStartTime() : "")
                + " | Barbero: " + (cita.getBarberName() != null ? cita.getBarberName() : "N/A")
                + " | Pago: " + (cita.getPaymentMethod() != null ? cita.getPaymentMethod() : "N/A");
        holder.tvCitaDetalle.setText(detalle);

        String estado = cita.getStatusName() != null ? cita.getStatusName() : "Sin estado";
        holder.tvCitaEstado.setText("Estado: " + estado);

        holder.btnCambiarEstado.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCambiarEstadoClick(cita);
            }
        });
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCitaCliente, tvCitaDetalle, tvCitaEstado;
        Button btnCambiarEstado;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCitaCliente = itemView.findViewById(R.id.tvCitaCliente);
            tvCitaDetalle = itemView.findViewById(R.id.tvCitaDetalle);
            tvCitaEstado = itemView.findViewById(R.id.tvCitaEstado);
            btnCambiarEstado = itemView.findViewById(R.id.btnCambiarEstado);
        }
    }
}
