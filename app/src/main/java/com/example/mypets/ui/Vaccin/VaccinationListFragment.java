package com.example.mypets.ui.Vaccin;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mypets.R;
import com.example.mypets.adapter.VaccinationAdapter;
import com.example.mypets.data.model.Vaccination;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class VaccinationListFragment extends Fragment {
    private RecyclerView recyclerView;
    private VaccinationAdapter adapter;
    private List<Vaccination> vaccinationList;
    private DatabaseReference dbRef;
    private String petId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vaccination_list, container, false);


        // Nhận petId từ Bundle
        if (getArguments() != null) {
            petId = getArguments().getString("petId");
            Log.d("VaccinationList", "petId: " + petId);

        }
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddVaccination);
        fabAdd.setOnClickListener(v -> openAddVaccinationDialog());

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewVaccinations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        vaccinationList = new ArrayList<>();
        adapter = new VaccinationAdapter(getContext(), vaccinationList);
        recyclerView.setAdapter(adapter);

        // Truy vấn vaccinations theo petId
        dbRef = FirebaseDatabase.getInstance()
                .getReference("pets")
                .child(petId)
                .child("vaccinations");


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vaccinationList.clear();
                Log.d("FirebaseDebug", "Đường dẫn: " + dbRef.toString());

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Lấy ID của vaccine
                    String vaccineId = dataSnapshot.getKey();
                    Log.d("FirebaseDebug", "Vaccine ID: " + vaccineId);

                    // Parse dữ liệu thành đối tượng Vaccination
                    Vaccination vaccination = dataSnapshot.getValue(Vaccination.class);
                    if (vaccination != null) {
                        Log.d("FirebaseDebug", "Dữ liệu vaccine: " + vaccination.toString());
                        vaccination.setId(dataSnapshot.getKey()); // Thêm dòng này
                        vaccinationList.add(vaccination);
                    } else {
                        Log.e("FirebaseDebug", "Không thể parse dữ liệu từ Firebase!");
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi: " + error.getMessage());
            }
        });
        adapter.setOnVaccinationActionListener(new VaccinationAdapter.OnVaccinationActionListener() {
            @Override
            public void onEditClick(Vaccination vaccination, int position) {
                openEditVaccinationDialog(vaccination);
            }

            @Override
            public void onDeleteClick(Vaccination vaccination) {

                deleteVaccination(vaccination);
            }
        });

        return view;
    }
    private void openAddVaccinationDialog() {
        AddVaccinationDialogFragment dialog = AddVaccinationDialogFragment.newInstance(petId);
        dialog.show(getParentFragmentManager(), "AddVaccinationDialog");
    }
    // Mở dialog chỉnh sửa
    private void openEditVaccinationDialog(Vaccination vaccination) {
        AddVaccinationDialogFragment dialog = AddVaccinationDialogFragment.newInstance(petId, vaccination);
        dialog.show(getParentFragmentManager(), "EditVaccinationDialog");
    }

    // Xóa vaccine
    private void deleteVaccination(Vaccination vaccination) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa vaccine")
                .setMessage("Bạn có chắc muốn xóa vaccine này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    DatabaseReference vaccineRef = FirebaseDatabase.getInstance()
                            .getReference("pets")
                            .child(petId)
                            .child("vaccinations")
                            .child(vaccination.getId());

                    vaccineRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                // Xóa vaccine khỏi danh sách bằng ID
                                int index = -1;
                                for (int i = 0; i < vaccinationList.size(); i++) {
                                    if (vaccinationList.get(i).getId().equals(vaccination.getId())) {
                                        index = i;
                                        break;
                                    }
                                }
                                if (index != -1) {
                                    vaccinationList.remove(index);
                                    adapter.notifyItemRemoved(index);
                                    Toast.makeText(requireContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}