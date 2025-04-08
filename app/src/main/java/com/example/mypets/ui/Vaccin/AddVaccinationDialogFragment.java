package com.example.mypets.ui.Vaccin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.mypets.R;
import com.example.mypets.data.model.Vaccination;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddVaccinationDialogFragment extends DialogFragment {
    private TextInputEditText etVaccineName, etDate, etNextDate;
    private MaterialButton btnDatePicker, btnNextDatePicker;
    private DatabaseReference dbRef;
    private String petId;
    private Vaccination editingVaccination;

    private final SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static AddVaccinationDialogFragment newInstance(String petId) {
        return newInstance(petId, null);
    }

    public static AddVaccinationDialogFragment newInstance(String petId, Vaccination vaccination) {
        AddVaccinationDialogFragment fragment = new AddVaccinationDialogFragment();
        Bundle args = new Bundle();
        args.putString("petId", petId);
        args.putSerializable("vaccination", vaccination);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_vaccination, container, false);
        initViews(view);
        setupFirebase();
        loadVaccinationData();
        setupClickListeners();
        return view;
    }

    private void initViews(View view) {
        etVaccineName = view.findViewById(R.id.etVaccineName);
        etDate = view.findViewById(R.id.etDate);
        etNextDate = view.findViewById(R.id.etNextDate);
        btnDatePicker = view.findViewById(R.id.btnDatePicker);
        btnNextDatePicker = view.findViewById(R.id.btnNextDatePicker);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveVaccination());
    }

    private void setupFirebase() {
        dbRef = FirebaseDatabase.getInstance().getReference("pets");
        petId = getArguments() != null ? getArguments().getString("petId") : "";
    }

    private void loadVaccinationData() {
        if (getArguments() != null && getArguments().getSerializable("vaccination") != null) {
            editingVaccination = (Vaccination) getArguments().getSerializable("vaccination");
            populateFields();
        }
    }

    private void populateFields() {
        if (editingVaccination != null) {
            etVaccineName.setText(editingVaccination.getVaccineName());
            etDate.setText(convertToDisplayFormat(editingVaccination.getDate()));
            etNextDate.setText(convertToDisplayFormat(editingVaccination.getNextDate()));
        }
    }

    private void setupClickListeners() {
        btnDatePicker.setOnClickListener(v -> showDatePicker(etDate));
        btnNextDatePicker.setOnClickListener(v -> showDatePicker(etNextDate));
    }

    private void showDatePicker(TextInputEditText targetField) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            dayOfMonth, month + 1, year);
                    targetField.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void saveVaccination() {
        if (!validateInput()) return;

        try {
            Vaccination vaccination = createVaccinationObject();
            saveToFirebase(vaccination);
        } catch (ParseException e) {
            showDateError();
        }
    }

    private boolean validateInput() {
        if (etVaccineName.getText().toString().trim().isEmpty()) {
            etVaccineName.setError("Vui lòng nhập tên vaccine");
            return false;
        }
        if (etDate.getText().toString().trim().isEmpty()) {
            etDate.setError("Vui lòng chọn ngày tiêm");
            return false;
        }
        return true;
    }

    private Vaccination createVaccinationObject() throws ParseException {
        Vaccination vaccination = new Vaccination();
        vaccination.setVaccineName(etVaccineName.getText().toString().trim());
        vaccination.setDate(convertToDbFormat(etDate.getText().toString().trim()));

        String nextDate = etNextDate.getText().toString().trim();
        if (!nextDate.isEmpty()) {
            vaccination.setNextDate(convertToDbFormat(nextDate));
        }

        if (editingVaccination != null) {
            vaccination.setId(editingVaccination.getId());
        }
        return vaccination;
    }

    private void saveToFirebase(Vaccination vaccination) {
        DatabaseReference vaccineRef = dbRef.child(petId).child("vaccinations");
        String vaccineId = vaccination.getId() != null ? vaccination.getId() : vaccineRef.push().getKey();

        vaccineRef.child(vaccineId).setValue(vaccination)
                .addOnSuccessListener(aVoid -> handleSaveSuccess())
                .addOnFailureListener(e -> handleSaveFailure(e));
    }

    private String convertToDisplayFormat(String dbDate) {
        if (dbDate == null || dbDate.isEmpty()) return "";
        try {
            Date date = dbFormat.parse(dbDate);
            return inputFormat.format(date);
        } catch (ParseException e) {
            return dbDate;
        }
    }

    private String convertToDbFormat(String inputDate) throws ParseException {
        Date date = inputFormat.parse(inputDate);
        return dbFormat.format(date);
    }

    private void handleSaveSuccess() {
        Toast.makeText(requireContext(), "Lưu thành công", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private void handleSaveFailure(Exception e) {
        Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void showDateError() {
        Toast.makeText(requireContext(),
                "Định dạng ngày không hợp lệ! Vui lòng sử dụng định dạng dd/MM/yyyy",
                Toast.LENGTH_LONG).show();
    }
}