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
import com.example.mypets.data.model.Vaccination;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VaccinationAdapter extends RecyclerView.Adapter<VaccinationAdapter.ViewHolder> {
    private List<Vaccination> vaccinationList;
    private Context context;
    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public VaccinationAdapter(Context context, List<Vaccination> vaccinationList) {
        this.context = context;
        this.vaccinationList = vaccinationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vaccination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vaccination vaccination = vaccinationList.get(position);

        holder.tvVaccineName.setText(vaccination.getVaccineName());

        // Xử lý định dạng ngày tháng
        holder.tvDate.setText("Ngày tiêm: " + formatDateForDisplay(vaccination.getDate()));

        // Xử lý ngày tiêm tiếp theo (có thể trống)
        String nextDate = formatDateForDisplay(vaccination.getNextDate());
        if (nextDate.isEmpty()) {
            holder.tvNextDate.setVisibility(View.GONE);
        } else {
            holder.tvNextDate.setText("Ngày tiêm tiếp theo: " + nextDate);
            holder.tvNextDate.setVisibility(View.VISIBLE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditClick(vaccination, position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteClick(vaccination);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vaccinationList.size();
    }

    private String formatDateForDisplay(String dbDate) {
        if (dbDate == null || dbDate.isEmpty()) {
            return "";
        }
        try {
            Date date = dbDateFormat.parse(dbDate);
            return displayDateFormat.format(date);
        } catch (ParseException e) {
            return dbDate; // Trả về nguyên bản nếu có lỗi
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVaccineName, tvDate, tvNextDate;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVaccineName = itemView.findViewById(R.id.tvVaccineName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNextDate = itemView.findViewById(R.id.tvNextDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnVaccinationActionListener {
        void onEditClick(Vaccination vaccination, int position);
        void onDeleteClick(Vaccination vaccination);
    }

    private OnVaccinationActionListener actionListener;

    public void setOnVaccinationActionListener(OnVaccinationActionListener listener) {
        this.actionListener = listener;
    }

    // Thêm phương thức cập nhật dữ liệu
    public void updateData(List<Vaccination> newList) {
        vaccinationList = newList;
        notifyDataSetChanged();
    }
}