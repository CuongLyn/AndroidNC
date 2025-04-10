package com.example.mypets.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.R;
import com.example.mypets.data.model.Schedule;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private List<Schedule> schedules;
    private Context context;
    private OnScheduleActionListener listener;

    public interface OnScheduleActionListener {
        void onEditClick(Schedule schedule);
        void onDeleteClick(Schedule schedule);
    }

    public ScheduleAdapter(List<Schedule> schedules, Context context) {
        this.schedules = schedules;
        this.context = context;
    }

    public void setOnScheduleActionListener(OnScheduleActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule schedule = schedules.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        holder.tvPetName.setText(schedule.getPetName());
        holder.tvActivity.setText(schedule.getActivity());
        holder.tvTime.setText(sdf.format(new Date(schedule.getTime())));
        holder.tvNote.setText(schedule.getNote());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(schedule);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(schedule);
        });
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPetName, tvActivity, tvTime, tvNote;
        MaterialButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPetName = itemView.findViewById(R.id.tv_pet_name);
            tvActivity = itemView.findViewById(R.id.tv_activity);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvNote = itemView.findViewById(R.id.tv_note);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}