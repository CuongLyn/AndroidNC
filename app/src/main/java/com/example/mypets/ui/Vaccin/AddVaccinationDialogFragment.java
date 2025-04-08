package com.example.mypets.ui.Vaccin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.mypets.R;
import com.example.mypets.data.model.Vaccination;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddVaccinationDialogFragment extends DialogFragment {
    private EditText etVaccineName, etDate, etNextDate;
    private DatabaseReference dbRef;
    private String petId;
    private Vaccination editingVaccination;

    // Thêm định dạng ngày tháng
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_vaccination, container, false);

        etVaccineName = view.findViewById(R.id.etVaccineName);
        etDate = view.findViewById(R.id.etDate);
        etNextDate = view.findViewById(R.id.etNextDate);
        Button btnSave = view.findViewById(R.id.btnSave);

        if (getArguments() != null) {
            petId = getArguments().getString("petId");
        }
        if (getArguments() != null && getArguments().getSerializable("vaccination") != null) {
            editingVaccination = (Vaccination) getArguments().getSerializable("vaccination");
            etVaccineName.setText(editingVaccination.getVaccineName());

            // Chuyển đổi ngày từ db format sang hiển thị
            etDate.setText(convertToDisplayFormat(editingVaccination.getDate()));
            etNextDate.setText(convertToDisplayFormat(editingVaccination.getNextDate()));
        }

        btnSave.setOnClickListener(v -> saveVaccination());
        return view;
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

    private void saveVaccination() {
        String vaccineName = etVaccineName.getText().toString().trim();
        String inputDate = etDate.getText().toString().trim();
        String inputNextDate = etNextDate.getText().toString().trim();

        if (vaccineName.isEmpty() || inputDate.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền tên vaccine và ngày tiêm", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Chuyển đổi ngày tháng sang định dạng database
            String dbDate = convertToDbFormat(inputDate);
            String dbNextDate = inputNextDate.isEmpty() ? "" : convertToDbFormat(inputNextDate);

            boolean isEditing = (editingVaccination != null);
            DatabaseReference vaccineRef = FirebaseDatabase.getInstance()
                    .getReference("pets")
                    .child(petId)
                    .child("vaccinations");

            Vaccination vaccination = new Vaccination();
            vaccination.setVaccineName(vaccineName);
            vaccination.setDate(dbDate);
            vaccination.setNextDate(dbNextDate);

            if (isEditing) {
                vaccination.setId(editingVaccination.getId());
                vaccineRef.child(editingVaccination.getId()).setValue(vaccination)
                        .addOnSuccessListener(aVoid -> handleSaveSuccess());
            } else {
                String newId = vaccineRef.push().getKey();
                vaccination.setId(newId);
                vaccineRef.child(newId).setValue(vaccination)
                        .addOnSuccessListener(aVoid -> handleSaveSuccess());
            }

        } catch (ParseException e) {
            Toast.makeText(getContext(), "Sai định dạng ngày! Vui lòng nhập dd/MM/yyyy", Toast.LENGTH_SHORT).show();
        }
    }

    private String convertToDbFormat(String inputDate) throws ParseException {
        Date date = inputFormat.parse(inputDate);
        return dbFormat.format(date);
    }

    private void handleSaveSuccess() {
        Toast.makeText(getContext(), "Lưu thành công", Toast.LENGTH_SHORT).show();
        dismiss();

    }

}