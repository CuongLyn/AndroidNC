package com.example.mypets.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private List<Date> dates;
    private OnDateClickListener listener;

    public interface OnDateClickListener {
        void onDateClick(Date date);
    }

    public CalendarAdapter(List<Date> dates, OnDateClickListener listener) {
        this.dates = dates;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Date date = dates.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("d", Locale.getDefault());
        holder.dayText.setText(sdf.format(date));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onDateClick(date);
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.tv_day);
        }
    }
}