package com.app.barberiaelcaballero.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.barberiaelcaballero.R;
import com.app.barberiaelcaballero.model.ServiceItem;
import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<ServiceItem> servicios = new ArrayList<>();

    public void setServicios(List<ServiceItem> nuevosServicios) {
        this.servicios = nuevosServicios != null ? nuevosServicios : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceItem servicio = servicios.get(position);

        String nombre = servicio.getName() != null ? servicio.getName() : "Servicio";
        holder.tvNombreServicio.setText(nombre);

        StringBuilder detalle = new StringBuilder();
        if (servicio.getPrice() != null) {
            detalle.append("$").append(servicio.getPrice());
        }
        if (servicio.getDurationMinutes() != null) {
            if (detalle.length() > 0) {
                detalle.append(" • ");
            }
            detalle.append(servicio.getDurationMinutes()).append(" min");
        }
        if (detalle.length() == 0) {
            detalle.append("Consultar precio y duración");
        }

        holder.tvPrecioDuracion.setText(detalle.toString());
    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreServicio;
        TextView tvPrecioDuracion;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreServicio = itemView.findViewById(R.id.tvNombreServicio);
            tvPrecioDuracion = itemView.findViewById(R.id.tvPrecioDuracion);
        }
    }
}
