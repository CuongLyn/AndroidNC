package com.example.mypets.ui.pet;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.R;
import com.example.mypets.adapter.PetAdapter;
import com.example.mypets.data.model.Pet.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyPetFragment extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private PetAdapter petAdapter;
    private List<Pet> petList;

    public MyPetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_pet, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        petList = new ArrayList<>();
        petAdapter = new PetAdapter(getContext(), petList);
        recyclerView.setAdapter(petAdapter);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("pets");

        // Load dữ liệu từ Firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                petList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Pet pet = snapshot.getValue(Pet.class);
                    if (pet != null) {
                        pet.setId(snapshot.getKey());
                        petList.add(pet);
                    }
                }
                petAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("MyPetFragment", "Load data failed: ", error.toException());
            }
        });

        // Xử lý khi click vào item để xem chi tiết
        petAdapter.setOnItemClickListener(pet -> {
            Bundle bundle = new Bundle();
            bundle.putString("id", pet.getId());
            bundle.putString("name", pet.getName());
            bundle.putString("loai", pet.getLoai());
            bundle.putInt("tuoi", pet.getTuoi());
            bundle.putString("gioiTinh", pet.getGioiTinh());
            bundle.putString("lichTiem", pet.getLichTiem());
            bundle.putString("lichKiemTra", pet.getLichKiemTraSucKhoe());

            NavHostFragment.findNavController(MyPetFragment.this)
                    .navigate(R.id.action_myPet_to_petInfo, bundle);
        });

        // Xử lý khi click vào nút sửa
        petAdapter.setOnEditClickListener(this::showEditDialog);

        return rootView;
    }

    private void showEditDialog(Pet pet) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chỉnh sửa thú cưng");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_pet, null);
        builder.setView(view);

        EditText nameEditText = view.findViewById(R.id.editTextName);
        EditText loaiEditText = view.findViewById(R.id.editTextLoai);
        EditText tuoiEditText = view.findViewById(R.id.editTextTuoi);
        EditText lichTiemEditText = view.findViewById(R.id.editTextLichTiem);
        EditText lichKiemTraEditText = view.findViewById(R.id.editTextLichKiemTra);
        RadioGroup gioiTinhRadioGroup = view.findViewById(R.id.radioGroupGioiTinh);

        nameEditText.setText(pet.getName());
        loaiEditText.setText(pet.getLoai());
        tuoiEditText.setText(String.valueOf(pet.getTuoi()));
        lichTiemEditText.setText(pet.getLichTiem());
        lichKiemTraEditText.setText(pet.getLichKiemTraSucKhoe());

        for (int i = 0; i < gioiTinhRadioGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) gioiTinhRadioGroup.getChildAt(i);
            if (rb.getText().toString().equals(pet.getGioiTinh())) {
                rb.setChecked(true);
                break;
            }
        }

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String name = nameEditText.getText().toString().trim();
            String loai = loaiEditText.getText().toString().trim();
            String tuoiStr = tuoiEditText.getText().toString().trim();
            String lichTiem = lichTiemEditText.getText().toString().trim();
            String lichKiemTra = lichKiemTraEditText.getText().toString().trim();

            int tuoi;
            try {
                tuoi = Integer.parseInt(tuoiStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Tuổi không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedGenderId = gioiTinhRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedGenderButton = view.findViewById(selectedGenderId);
            String gioiTinh = selectedGenderButton.getText().toString();

            DatabaseReference petRef = FirebaseDatabase.getInstance().getReference("pets").child(pet.getId());
            petRef.setValue(new Pet(pet.getId(), name, loai, tuoi, gioiTinh, lichTiem, lichKiemTra))
                    .addOnSuccessListener(unused ->
                            Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
