package com.example.mypets.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.R;
import com.example.mypets.data.model.MedicalRecord;

import java.util.List;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.ViewHolder> {

    private Context context;
    private List<MedicalRecord> recordList;
    private OnMedicalActionListener listener;

    public interface OnMedicalActionListener {
        void onEdit(MedicalRecord record, int position);
        void onDelete(MedicalRecord record);
    }

    public MedicalRecordAdapter(Context context, List<MedicalRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    public void setOnMedicalActionListener(OnMedicalActionListener listener) {
        this.listener = listener;
    }

    public void updateData(List<MedicalRecord> newList) {
        this.recordList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medical_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicalRecord record = recordList.get(position);

        holder.tvDate.setText("Ngày khám: " + record.getDate());
        holder.tvVetName.setText("Bác sĩ: " + record.getVetName());
        holder.tvClinicName.setText("Phòng khám: " + record.getClinicName());
        holder.tvReason.setText("Lý do: " + record.getReason());
        holder.tvDiagnosis.setText("Chẩn đoán: " + record.getDiagnosis());
        holder.tvSymptoms.setText("Triệu chứng: " + record.getSymptoms());
        holder.tvTreatment.setText("Điều trị: " + record.getTreatment());
        holder.tvPrescription.setText("Đơn thuốc: " + record.getPrescription());
        holder.tvNote.setText("Ghi chú: " + record.getNote());

        holder.btnMenu.setOnClickListener(v -> showPopupMenu(v, record, position));
    }

    private void showPopupMenu(View view, MedicalRecord record, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.vaccination_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                if (listener != null) listener.onEdit(record, position);
                return true;
            } else if (item.getItemId() == R.id.menu_delete) {
                if (listener != null) listener.onDelete(record);
                return true;
            }
            return false;
        });

        popup.show();
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvVetName, tvClinicName, tvReason, tvDiagnosis, tvSymptoms, tvTreatment, tvPrescription, tvNote;
        ImageView btnMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvVetName = itemView.findViewById(R.id.tvVetName);
            tvClinicName = itemView.findViewById(R.id.tvClinicName);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvDiagnosis = itemView.findViewById(R.id.tvDiagnosis);
            tvSymptoms = itemView.findViewById(R.id.tvSymptoms);
            tvTreatment = itemView.findViewById(R.id.tvTreatment);
            tvPrescription = itemView.findViewById(R.id.tvPrescription);
            tvNote = itemView.findViewById(R.id.tvNote);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }
    }
}
