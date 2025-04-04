package com.example.mypets.ui.pet;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mypets.R;
import com.example.mypets.data.model.Pet;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddPetFragment extends Fragment {

    private EditText nameEditText, loaiEditText, tuoiEditText, imageUrlEditText, lichTiemEditText, lichKiemTraSucKhoeEditText;
    private Button addButton;
    private DatabaseReference mDatabase;

    public AddPetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_pet, container, false);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference("pets");

        nameEditText = view.findViewById(R.id.nameEditText);
        loaiEditText = view.findViewById(R.id.loaiEditText);
        tuoiEditText = view.findViewById(R.id.tuoiEditText);
        imageUrlEditText = view.findViewById(R.id.imageUrlEditText);
        lichTiemEditText = view.findViewById(R.id.lichTiemEditText);
        lichKiemTraSucKhoeEditText = view.findViewById(R.id.lichKiemTraSucKhoeEditText);
        addButton = view.findViewById(R.id.addButton);

        addButton.setOnClickListener(v -> addPet());

        // Set click listeners for DatePickers
        lichTiemEditText.setOnClickListener(v -> showDatePickerDialog(lichTiemEditText));
        lichKiemTraSucKhoeEditText.setOnClickListener(v -> showDatePickerDialog(lichKiemTraSucKhoeEditText));

        return view;
    }

    private void addPet() {
        String name = nameEditText.getText().toString().trim();
        String loai = loaiEditText.getText().toString().trim();
        String tuoiStr = tuoiEditText.getText().toString().trim();
        String imageUrl = imageUrlEditText.getText().toString().trim();
        String lichTiem = lichTiemEditText.getText().toString().trim();
        String lichKiemTraSucKhoe = lichKiemTraSucKhoeEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(loai) || TextUtils.isEmpty(tuoiStr) || TextUtils.isEmpty(imageUrl)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra và xử lý ngoại lệ khi chuyển đổi tuổi
        int tuoi = 0;
        try {
            tuoi = Integer.parseInt(tuoiStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid age", Toast.LENGTH_SHORT).show();
            return;  // Dừng nếu tuổi không hợp lệ
        }
        String petId = mDatabase.push().getKey();  // Auto-generate id
        Pet pet = new Pet(petId, name, loai, tuoi, imageUrl, lichTiem, lichKiemTraSucKhoe);

        if (petId != null) {

            pet.setId(petId);  // Set the generated ID as String
            mDatabase.child(petId).setValue(pet);
            Toast.makeText(getContext(), "Pet added successfully!", Toast.LENGTH_SHORT).show();
        }
    }


    private void showDatePickerDialog(EditText editText) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    // Format the selected date and set it to the EditText
                    String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editText.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
}
