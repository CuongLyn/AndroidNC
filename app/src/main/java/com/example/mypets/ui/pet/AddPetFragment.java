package com.example.mypets.ui.pet;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mypets.R;
import com.example.mypets.data.model.Pet;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddPetFragment extends Fragment {

    private EditText nameEditText, loaiEditText, tuoiEditText, lichTiemEditText, lichKiemTraSucKhoeEditText;
    private RadioGroup gioiTinhRadioGroup;
    private Button addButton;
    private DatabaseReference mDatabase;

    public AddPetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_pet, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("pets");

        nameEditText = view.findViewById(R.id.editTextName);
        loaiEditText = view.findViewById(R.id.editTextLoai);
        tuoiEditText = view.findViewById(R.id.editTextTuoi);
        lichTiemEditText = view.findViewById(R.id.editTextLichTiem);
        lichKiemTraSucKhoeEditText = view.findViewById(R.id.editTextLichKiemTra);
        gioiTinhRadioGroup = view.findViewById(R.id.radioGroupGioiTinh);
        addButton = new Button(getContext());
        addButton.setText("Thêm");
        ((ViewGroup) view).addView(addButton); // Thêm nút nếu XML chưa có

        addButton.setOnClickListener(v -> addPet());

        lichTiemEditText.setOnClickListener(v -> showDatePickerDialog(lichTiemEditText));
        lichKiemTraSucKhoeEditText.setOnClickListener(v -> showDatePickerDialog(lichKiemTraSucKhoeEditText));

        return view;
    }

    private void addPet() {
        String name = nameEditText.getText().toString().trim();
        String loai = loaiEditText.getText().toString().trim();
        String tuoiStr = tuoiEditText.getText().toString().trim();
        String lichTiem = lichTiemEditText.getText().toString().trim();
        String lichKiemTraSucKhoe = lichKiemTraSucKhoeEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(loai) || TextUtils.isEmpty(tuoiStr)) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int tuoi;
        try {
            tuoi = Integer.parseInt(tuoiStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Tuổi không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedGenderId = gioiTinhRadioGroup.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(getContext(), "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedGenderButton = getView().findViewById(selectedGenderId);
        String gioiTinh = selectedGenderButton.getText().toString();

        String petId = mDatabase.push().getKey();
        if (petId != null) {
            Pet pet = new Pet(petId, name, loai, tuoi, gioiTinh, lichTiem, lichKiemTraSucKhoe);
            mDatabase.child(petId).setValue(pet);
            Toast.makeText(getContext(), "Thêm thú cưng thành công!", Toast.LENGTH_SHORT).show();

            // ✅ Xóa dữ liệu nhập sau khi thêm
            nameEditText.setText("");
            loaiEditText.setText("");
            tuoiEditText.setText("");
            lichTiemEditText.setText("");
            lichKiemTraSucKhoeEditText.setText("");
            gioiTinhRadioGroup.clearCheck();
        }
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editText.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }
}
