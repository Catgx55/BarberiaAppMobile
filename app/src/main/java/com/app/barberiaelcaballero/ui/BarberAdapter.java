package com.app.barberiaelcaballero.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.barberiaelcaballero.R;
import com.app.barberiaelcaballero.model.BarberItem;
import java.util.ArrayList;
import java.util.List;

public class BarberAdapter extends RecyclerView.Adapter<BarberAdapter.BarberViewHolder> {

    private List<BarberItem> barberos = new ArrayList<>();

    public void setBarberos(List<BarberItem> nuevosBarberos) {
        this.barberos = nuevosBarberos != null ? nuevosBarberos : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BarberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_barber, parent, false);
        return new BarberViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull BarberViewHolder holder, int position) {
        BarberItem barbero = barberos.get(position);

        String nombre = barbero.getName() != null ? barbero.getName() : "Barbero";
        holder.tvNombreBarbero.setText(nombre);

        String especialidad = (barbero.getSpecialty() != null && !barbero.getSpecialty().isEmpty())
                ? barbero.getSpecialty()
                : "Barbero profesional";
        holder.tvEspecialidadBarbero.setText(especialidad);
    }

    @Override
    public int getItemCount() {
        return barberos.size();
    }

    public static class BarberViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreBarbero;
        TextView tvEspecialidadBarbero;

        public BarberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreBarbero = itemView.findViewById(R.id.tvNombreBarbero);
            tvEspecialidadBarbero = itemView.findViewById(R.id.tvEspecialidadBarbero);
        }
    }
}
