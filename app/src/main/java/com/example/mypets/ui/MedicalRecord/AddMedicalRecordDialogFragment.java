package com.example.mypets.ui.MedicalRecord;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.mypets.R;
import com.example.mypets.data.model.MedicalRecord;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddMedicalRecordDialogFragment extends DialogFragment {
    private TextInputEditText  etDate, etVetName, etClinicName, etReason, etDiagnosis, etSymptoms, etTreatment, etPrescription, etNote;
    private MaterialButton btnDatePicker;
    private DatabaseReference dbRef;
    private String petId;
    private MedicalRecord editingRecord;

    private final SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static AddMedicalRecordDialogFragment newInstance(String petId) {
        return newInstance(petId, null);
    }

    public static AddMedicalRecordDialogFragment newInstance(String petId, MedicalRecord record) {
        AddMedicalRecordDialogFragment fragment = new AddMedicalRecordDialogFragment();
        Bundle args = new Bundle();
        args.putString("petId", petId);
        args.putSerializable("record", record);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_medical_record, container, false);
        initViews(view);
        setupFirebase();
        loadMedicalRecordData();
        setupClickListeners();
        return view;
    }

    private void initViews(View view) {
        etDate = view.findViewById(R.id.etDate);
        etVetName = view.findViewById(R.id.etVetName);
        etClinicName = view.findViewById(R.id.etClinicName);
        etReason = view.findViewById(R.id.etReason);
        etDiagnosis = view.findViewById(R.id.etDiagnosis);
        etSymptoms = view.findViewById(R.id.etSymptoms);
        etTreatment = view.findViewById(R.id.etTreatment);
        etPrescription = view.findViewById(R.id.etPrescription);
        etNote = view.findViewById(R.id.etNote);
        btnDatePicker = view.findViewById(R.id.btnDatePicker);

        MaterialButton btnSave = view.findViewById(R.id.btnSaveMedicalRecord);
        btnSave.setOnClickListener(v -> saveMedicalRecord());
    }

    private void setupFirebase() {
        dbRef = FirebaseDatabase.getInstance().getReference("pets");
        petId = getArguments() != null ? getArguments().getString("petId") : "";


    }

    private void loadMedicalRecordData() {
        if (getArguments() != null && getArguments().getSerializable("record") != null) {
            editingRecord = (MedicalRecord) getArguments().getSerializable("record");
            populateFields();
        }
    }

    private void populateFields() {
        if (editingRecord != null) {
            etDate.setText(convertToDisplayFormat(editingRecord.getDate()));
            etVetName.setText(editingRecord.getVetName());
            etClinicName.setText(editingRecord.getClinicName());
            etReason.setText(editingRecord.getReason());
            etDiagnosis.setText(editingRecord.getDiagnosis());
            etSymptoms.setText(editingRecord.getSymptoms());
            etTreatment.setText(editingRecord.getTreatment());
            etPrescription.setText(editingRecord.getPrescription());
            etNote.setText(editingRecord.getNote());
        }
    }

    private void setupClickListeners() {
        btnDatePicker.setOnClickListener(v -> showDatePicker(etDate));
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

    private void saveMedicalRecord() {
        if (!validateInput()) return;

        try {
            MedicalRecord record = createMedicalRecordObject();
            saveToFirebase(record);
        } catch (ParseException e) {
            showDateError();
        }
    }

    private boolean validateInput() {

        if (etDate.getText().toString().trim().isEmpty()) {
            etDate.setError("Vui lòng chọn ngày");
            return false;
        }
        return true;
    }

    private MedicalRecord createMedicalRecordObject() throws ParseException {
        MedicalRecord record = new MedicalRecord();
        record.setClinicName(etClinicName.getText().toString().trim());
        record.setDate(convertToDbFormat(etDate.getText().toString().trim()));
        record.setVetName(etVetName.getText().toString().trim());
        record.setReason(etReason.getText().toString().trim());
        record.setDiagnosis(etDiagnosis.getText().toString().trim()); // ✅ sửa ở đây
        record.setSymptoms(etSymptoms.getText().toString().trim());
        record.setTreatment(etTreatment.getText().toString().trim());
        record.setPrescription(etPrescription.getText().toString().trim());
        record.setNote(etNote.getText().toString().trim()); // ✅ sửa ở đây

        if (editingRecord != null) {
            record.setMedicalRecordId(editingRecord.getMedicalRecordId());
        }

        return record;


    }

    private void saveToFirebase(MedicalRecord record) {
        DatabaseReference recordRef = dbRef.child(petId).child("medicalRecords");
        String recordId = record.getMedicalRecordId() != null ? record.getMedicalRecordId() : recordRef.push().getKey();

        recordRef.child(recordId).setValue(record)
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
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.95);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

}
