package com.example.mypets.ui.Vaccin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.R;
import com.example.mypets.adapter.VaccinationAdapter;
import com.example.mypets.data.model.Vaccination;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VaccinationListFragment extends Fragment {
    private static final String TAG = "VaccinationListFragment";

    private RecyclerView recyclerView;
    private VaccinationAdapter adapter;
    private List<Vaccination> vaccinationList;
    private DatabaseReference dbRef;
    private String petId;
    private ValueEventListener valueEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vaccination_list, container, false);
        initViews(view);
        getPetIdFromArguments();
        setupRecyclerView();
        setupFirebase();
        return view;
    }

    private void initViews(View view) {
        ExtendedFloatingActionButton fabAdd = view.findViewById(R.id.fabAddVaccination);
        fabAdd.setOnClickListener(v -> openAddVaccinationDialog());
        recyclerView = view.findViewById(R.id.recyclerViewVaccinations);
    }

    private void getPetIdFromArguments() {
        Bundle args = getArguments();
        if (args != null) {
            petId = args.getString("petId");
            Log.d(TAG, "Received petId: " + petId);
        }
    }

    private void setupRecyclerView() {
        vaccinationList = new ArrayList<>();
        adapter = new VaccinationAdapter(requireContext(), vaccinationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnVaccinationActionListener(new VaccinationAdapter.OnVaccinationActionListener() {
            @Override
            public void onEditClick(Vaccination vaccination, int position) {
                openEditVaccinationDialog(vaccination);
            }

            @Override
            public void onDeleteClick(Vaccination vaccination) {
                showDeleteConfirmationDialog(vaccination);
            }
        });
    }

    private void setupFirebase() {
        dbRef = FirebaseDatabase.getInstance()
                .getReference("pets")
                .child(petId)
                .child("vaccinations");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                handleDataChange(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handleDatabaseError(error);
            }
        };
        dbRef.addValueEventListener(valueEventListener);
    }

    private void handleDataChange(DataSnapshot snapshot) {
        vaccinationList.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            processVaccinationData(dataSnapshot);
        }
        adapter.updateData(vaccinationList);
    }

    private void processVaccinationData(DataSnapshot dataSnapshot) {
        String vaccineId = dataSnapshot.getKey();
        Vaccination vaccination = dataSnapshot.getValue(Vaccination.class);

        if (vaccination != null) {
            vaccination.setId(vaccineId);
            Log.d(TAG, "Adding vaccination: " + vaccination);
            vaccinationList.add(vaccination);
        } else {
            Log.e(TAG, "Failed to parse vaccination data for key: " + vaccineId);
        }
    }

    private void handleDatabaseError(DatabaseError error) {
        Log.e(TAG, "Database error: " + error.getMessage());
        Toast.makeText(requireContext(), "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void openAddVaccinationDialog() {
        AddVaccinationDialogFragment dialog = AddVaccinationDialogFragment.newInstance(petId);
        dialog.show(getParentFragmentManager(), "AddVaccinationDialog");
    }

    private void openEditVaccinationDialog(Vaccination vaccination) {
        AddVaccinationDialogFragment dialog = AddVaccinationDialogFragment.newInstance(petId, vaccination);
        dialog.show(getParentFragmentManager(), "EditVaccinationDialog");
    }

    private void showDeleteConfirmationDialog(Vaccination vaccination) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa vaccine")
                .setMessage("Bạn có chắc muốn xóa vaccine này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteVaccination(vaccination))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteVaccination(Vaccination vaccination) {
        DatabaseReference vaccineRef = dbRef.child(vaccination.getId());
        vaccineRef.removeValue()
                .addOnSuccessListener(aVoid -> handleDeleteSuccess(vaccination))
                .addOnFailureListener(e -> handleDeleteFailure(e));
    }

    private void handleDeleteSuccess(Vaccination vaccination) {
        int position = vaccinationList.indexOf(vaccination);
        if (position != -1) {
            vaccinationList.remove(position);
            adapter.notifyItemRemoved(position);
        }
        Toast.makeText(requireContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show();
    }

    private void handleDeleteFailure(Exception e) {
        Log.e(TAG, "Delete failed: " + e.getMessage());
        Toast.makeText(requireContext(), "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (valueEventListener != null) {
            dbRef.removeEventListener(valueEventListener);
        }
    }
}