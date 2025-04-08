package com.example.mypets.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.R;
import com.example.mypets.data.model.HealthRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthRecordAdapter extends RecyclerView.Adapter<HealthRecordAdapter.ViewHolder> {
    private final List<HealthRecord> records;

    public HealthRecordAdapter(List<HealthRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_health_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthRecord record = records.get(position);

        // Format date
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = inputFormat.parse(record.getDate());
            holder.tvDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            holder.tvDate.setText(record.getDate());
        }

        holder.tvDiagnosis.setText("Chẩn đoán: " + record.getDiagnosis());
        holder.tvSymptoms.setText("Triệu chứng: " + record.getSymptoms());
        holder.tvWeight.setText(String.format("Cân nặng: %.1f kg", record.getWeight()));
        holder.tvHeight.setText(String.format("Chiều cao: %d cm", record.getHeight()));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDiagnosis, tvWeight, tvSymptoms,tvHeight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvSymptoms = itemView.findViewById(R.id.tv_symptoms);
            tvHeight = itemView.findViewById(R.id.tv_height);
            tvDiagnosis = itemView.findViewById(R.id.tv_diagnosis);
            tvWeight = itemView.findViewById(R.id.tv_weight);
        }
    }
}