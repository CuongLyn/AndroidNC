package com.example.mypets.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.R;
import com.example.mypets.data.model.Clinic;

import java.util.List;

public class ClinicAdapter extends RecyclerView.Adapter<ClinicAdapter.ViewHolder> {
    private final List<Clinic> clinics;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Clinic clinic);
    }

    public ClinicAdapter(List<Clinic> clinics, OnItemClickListener listener) {
        this.clinics = clinics;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clinic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Clinic clinic = clinics.get(position);
        holder.tvClinicName.setText(clinic.getName());
        holder.tvDistance.setText(String.format("%.1f km", clinic.getDistance()));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(clinic));
    }

    @Override
    public int getItemCount() {
        return clinics.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClinicName, tvDistance;

        ViewHolder(View itemView) {
            super(itemView);
            tvClinicName = itemView.findViewById(R.id.tvClinicName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }
}