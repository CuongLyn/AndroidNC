package com.example.mypets.ui.PetHealthRecord;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypets.R;
import com.example.mypets.data.model.HealthRecord;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddHealthRecordDialogFragment extends DialogFragment {
    private EditText etWeight, etHeight, etDiagnosis, etSymptoms;
    private TextView tvDate;
    private Button btnSave;
    private OnHealthRecordSavedListener listener;
    private String petId;

    public interface OnHealthRecordSavedListener {
        void onHealthRecordSaved();
    }

    public static AddHealthRecordDialogFragment newInstance(String petId) {
        AddHealthRecordDialogFragment fragment = new AddHealthRecordDialogFragment();
        Bundle args = new Bundle();
        args.putString("petId", petId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnHealthRecordSavedListener(OnHealthRecordSavedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            petId = getArguments().getString("petId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_health_record_dialog, container, false);

        etWeight = view.findViewById(R.id.etWeight);
        etHeight = view.findViewById(R.id.etHeight);
        etDiagnosis = view.findViewById(R.id.etDiagnosis);
        etSymptoms = view.findViewById(R.id.etSymptoms);
        tvDate = view.findViewById(R.id.tvDate);
        btnSave = view.findViewById(R.id.btnSave);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        tvDate.setText(currentDate);

        btnSave.setOnClickListener(v -> saveHealthRecord());

        return view;
    }

    private void saveHealthRecord() {
        String date = tvDate.getText().toString();
        String weightStr = etWeight.getText().toString();
        String heightStr = etHeight.getText().toString();
        String diagnosis = etDiagnosis.getText().toString();
        String symptoms = etSymptoms.getText().toString();

        // Validate
        if (weightStr.isEmpty()) {
            etWeight.setError("Vui lòng nhập cân nặng");
            return;
        }
        if (heightStr.isEmpty()) {
            etHeight.setError("Vui lòng nhập chiều cao");
            return;
        }

        float weight = Float.parseFloat(weightStr);
        int height = Integer.parseInt(heightStr);

        HealthRecord record = new HealthRecord(date, diagnosis, symptoms, weight, height);

        if (petId == null) {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy thú cưng", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference healthRef = database.child("pets").child(petId).child("health_records");
        String recordId = healthRef.push().getKey();

        if (recordId == null) {
            Toast.makeText(getContext(), "Lỗi: Không thể tạo bản ghi", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        healthRef.child(recordId).setValue(record)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Lưu thành công", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onHealthRecordSaved();
                    }
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setLayout(width, height);
        }
    }

}
