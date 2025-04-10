package com.example.mypets.ui.MedicalRecord;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.example.mypets.adapter.MedicalRecordAdapter;
import com.example.mypets.data.model.MedicalRecord;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecordFragment extends Fragment {
    private static final String TAG = "MedicalRecordFragment";

    private RecyclerView recyclerView;
    private MedicalRecordAdapter adapter;
    private List<MedicalRecord> medicalRecordList;
    private DatabaseReference dbRef;
    private String petId = "";
    private ValueEventListener valueEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_record, container, false);
        initViews(view);
        getPetIdFromArguments();
        setupRecyclerView();
        setupFirebase();
        return view;
    }

    private void initViews(View view) {
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddMedicalhRecord);
        fabAdd.setOnClickListener(v -> openAddDialog());
        recyclerView = view.findViewById(R.id.recyclerViewMedicalRecord);
    }

    private void getPetIdFromArguments() {
        Bundle args = getArguments();
        if (args != null) {
            petId = args.getString("petId");
            Log.d(TAG, "Received petId: " + petId);
        }
    }

    private void setupRecyclerView() {
        medicalRecordList = new ArrayList<>();
        adapter = new MedicalRecordAdapter(requireContext(), medicalRecordList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnMedicalActionListener(new MedicalRecordAdapter.OnMedicalActionListener() {
            @Override
            public void onEdit(MedicalRecord record, int position) {
                openEditDialog(record);
            }

            @Override
            public void onDelete(MedicalRecord record) {
                confirmDelete(record);
            }
        });
    }

    private void setupFirebase() {
        dbRef = FirebaseDatabase.getInstance()
                .getReference("pets")
                .child(petId)
                .child("medicalRecords");

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
        Log.d(TAG, "DataSnapshot exists: " + snapshot.exists());
        Log.d(TAG, "Children count: " + snapshot.getChildrenCount());

        medicalRecordList.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            processMedicalData(dataSnapshot);
        }
        adapter.updateData(medicalRecordList);
    }

    private void processMedicalData(DataSnapshot dataSnapshot) {
        String recordId = dataSnapshot.getKey();
        MedicalRecord record = dataSnapshot.getValue(MedicalRecord.class);

        if (record != null) {
            record.setMedicalRecordId(recordId);
            Log.d(TAG, "Adding record: " + record);
            medicalRecordList.add(record);
        } else {
            Log.e(TAG, "Failed to parse medical record for key: " + recordId);
        }
    }

    private void handleDatabaseError(DatabaseError error) {
        Log.e(TAG, "Database error: " + error.getMessage());
        Toast.makeText(requireContext(), "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void openAddDialog() {
        AddMedicalRecordDialogFragment dialog = AddMedicalRecordDialogFragment.newInstance(petId);
        dialog.show(getParentFragmentManager(), "AddMedicalRecordDialog");
    }

    private void openEditDialog(MedicalRecord record) {
        AddMedicalRecordDialogFragment dialog = AddMedicalRecordDialogFragment.newInstance(petId, record);
        dialog.show(getParentFragmentManager(), "AddMedicalRecordDialog");
    }

    private void confirmDelete(MedicalRecord record) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa hồ sơ")
                .setMessage("Bạn có chắc muốn xóa hồ sơ này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteRecord(record))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteRecord(MedicalRecord record) {
        dbRef.child(record.getMedicalRecordId()).removeValue()
                .addOnSuccessListener(aVoid -> handleDeleteSuccess(record))
                .addOnFailureListener(this::handleDeleteFailure);
    }

    private void handleDeleteSuccess(MedicalRecord record) {
        int position = medicalRecordList.indexOf(record);
        if (position != -1) {
            medicalRecordList.remove(position);
            adapter.notifyItemRemoved(position);
        }
        Toast.makeText(requireContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
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
