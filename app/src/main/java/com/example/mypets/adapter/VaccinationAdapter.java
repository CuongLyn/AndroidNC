package com.example.mypets.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
    private OnVaccinationActionListener actionListener;

    public interface OnVaccinationActionListener {
        void onEditClick(Vaccination vaccination, int position);
        void onDeleteClick(Vaccination vaccination);
    }

    public VaccinationAdapter(Context context, List<Vaccination> vaccinationList) {
        this.context = context;
        this.vaccinationList = vaccinationList;
    }

    public void setOnVaccinationActionListener(OnVaccinationActionListener listener) {
        this.actionListener = listener;
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

        // Set basic information
        holder.tvVaccineName.setText(vaccination.getVaccineName());
        holder.tvDate.setText(formatDateLabel("Ngày tiêm: ", vaccination.getDate()));

        // Handle next date
        String nextDate = formatDateForDisplay(vaccination.getNextDate());
        if (nextDate.isEmpty()) {
            holder.tvNextDate.setVisibility(View.GONE);
        } else {
            holder.tvNextDate.setText(formatDateLabel("Ngày tiếp theo: ", vaccination.getNextDate()));
            holder.tvNextDate.setVisibility(View.VISIBLE);
        }



        // Setup menu click
        holder.btnMenu.setOnClickListener(v -> showPopupMenu(v, vaccination, position));
    }

    private void showPopupMenu(View view, Vaccination vaccination, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.vaccination_menu);

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_edit) {
                if (actionListener != null) {
                    actionListener.onEditClick(vaccination, position);
                }
                return true;
            }
            else if (itemId == R.id.menu_delete) {
                if (actionListener != null) {
                    actionListener.onDeleteClick(vaccination);
                }
                return true;
            }

            return false;
        });
        popup.show();
    }

    private String formatDateLabel(String prefix, String date) {
        return prefix + formatDateForDisplay(date);
    }

    private String formatDateForDisplay(String dbDate) {
        if (dbDate == null || dbDate.isEmpty()) {
            return "";
        }
        try {
            Date date = dbDateFormat.parse(dbDate);
            return displayDateFormat.format(date);
        } catch (ParseException e) {
            return dbDate;
        }
    }



    @Override
    public int getItemCount() {
        return vaccinationList.size();
    }

    public void updateData(List<Vaccination> newList) {
        vaccinationList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVaccineName, tvDate, tvNextDate;
        ImageView btnMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVaccineName = itemView.findViewById(R.id.tvVaccineName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNextDate = itemView.findViewById(R.id.tvNextDate);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }
    }
}