package com.example.mypets.ui.clinics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.mypets.R;
import com.example.mypets.data.model.Clinic;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ClinicDetailBottomSheet extends BottomSheetDialogFragment {
    private static final String ARG_CLINIC = "clinic";
    private Clinic clinic;

    // Constructor không tham số (bắt buộc)
    public ClinicDetailBottomSheet() {}

    // Phương thức tạo instance mới
    public static ClinicDetailBottomSheet newInstance(Clinic clinic) {
        ClinicDetailBottomSheet fragment = new ClinicDetailBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CLINIC, clinic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clinic = (Clinic) getArguments().getSerializable(ARG_CLINIC);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_clinic_detail, container, false);

        // Hiển thị thông tin phòng khám
        if (clinic != null) {
            TextView tvName = view.findViewById(R.id.tvClinicName);
            TextView tvAddress = view.findViewById(R.id.tvAddress);
            TextView tvPhone = view.findViewById(R.id.tvPhone);
            TextView tvHours = view.findViewById(R.id.tvWorkingHours);

            tvName.setText(clinic.getName());
            tvAddress.setText(clinic.getAddress());
            tvPhone.setText("Điện thoại: " + clinic.getPhone());
            tvHours.setText("Giờ làm việc: " + clinic.getWorkingHours());
        }

        return view;
    }
}