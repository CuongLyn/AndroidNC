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
    private EditText etWeight, etNotes;
    private TextView tvDate;
    private Button btnSave;
    private OnHealthRecordSavedListener listener;
    private String petId; // Thêm trường petId

    public interface OnHealthRecordSavedListener {
        void onHealthRecordSaved();
    }

    // Phương thức khởi tạo với petId
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
        // Lấy petId từ arguments
        if (getArguments() != null) {
            petId = getArguments().getString("petId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_health_record_dialog, container, false);

        etWeight = view.findViewById(R.id.etWeight);
        tvDate = view.findViewById(R.id.tvDate);
        btnSave = view.findViewById(R.id.btnSave);

        // Set ngày hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        tvDate.setText(currentDate);

        btnSave.setOnClickListener(v -> saveHealthRecord());
        return view;
    }

    private void saveHealthRecord() {
        String weightStr = etWeight.getText().toString();
        String date = tvDate.getText().toString();

        if (weightStr.isEmpty()) {
            etWeight.setError("Vui lòng nhập cân nặng");
            return;
        }

        // Tạo đối tượng HealthRecord
        HealthRecord record = new HealthRecord();
        record.setDate(date);
        record.setWeight(Float.parseFloat(weightStr));

        // Kiểm tra petId
        if (petId == null) {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy thú cưng", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        // Lưu vào Firebase
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
                        listener.onHealthRecordSaved(); // Gọi callback để cập nhật UI
                    }
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                });
    }
}