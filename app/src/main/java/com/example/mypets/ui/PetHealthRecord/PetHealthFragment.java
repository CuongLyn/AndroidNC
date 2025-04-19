package com.example.mypets.ui.PetHealthRecord;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.R;
import com.example.mypets.adapter.HealthRecordAdapter;
import com.example.mypets.chart.DateAxisValueFormatter;
import com.example.mypets.data.model.HealthRecord;
import com.example.mypets.databinding.FragmentPetHealthBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PetHealthFragment extends Fragment {
    private FragmentPetHealthBinding binding;
    private LineChart lineChart;
    private RecyclerView recyclerView;
    private DatabaseReference database;
    private String petId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPetHealthBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineChart = binding.lineChart;
        recyclerView = binding.recyclerViewHealthRecords;

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        database = FirebaseDatabase.getInstance().getReference();

        // Lấy dữ liệu từ Firebase
        loadHealthData();

        FloatingActionButton fabAdd = binding.fabAddHealthRecord;
        fabAdd.setOnClickListener(v -> showAddHealthRecordDialog());
    }
    private void showAddHealthRecordDialog() {
        AddHealthRecordDialogFragment dialog = AddHealthRecordDialogFragment.newInstance(petId);
        dialog.setOnHealthRecordSavedListener(() -> {
            loadHealthData(); // Load lại dữ liệu sau khi lưu
        });
        dialog.show(getParentFragmentManager(), "AddHealthRecordDialog");
    }

    private void loadHealthData() {
        Bundle args = getArguments();
        if (args == null) {
            Log.e("FIREBASE_DEBUG", "Bundle args is null");
            return;
        }
        petId = args.getString("petId");
        if (petId == null) {
            Log.e("FIREBASE_DEBUG", "petId is null");
            return;
        }

        DatabaseReference healthRef = database.child("pets").child(petId).child("health_records");
        healthRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HealthRecord> records = new ArrayList<>();
                for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                    HealthRecord record = recordSnapshot.getValue(HealthRecord.class);
                    if (record != null) {
                        records.add(record);
                    }
                }
                updateUI(records);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(List<HealthRecord> records) {
        // Sắp xếp theo ngày tăng dần
        Collections.sort(records, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));

        List<Entry> entries = new ArrayList<>();
        for (HealthRecord record : records) {
            long timestamp = record.getTimestamp();
            if (timestamp > 0) {
                // Sử dụng timestamp trực tiếp (đơn vị milliseconds)
                entries.add(new Entry(timestamp, record.getWeight()));
            }
        }

        if (!entries.isEmpty()) {
            LineDataSet dataSet = new LineDataSet(entries, "Cân nặng (kg)");
            dataSet.setColor(Color.BLUE);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setCircleColor(Color.RED);
            dataSet.setCircleRadius(4f);

            // Cấu hình trục X
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new DateAxisValueFormatter());
            xAxis.setGranularity(2678400000f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setLabelRotationAngle(-45);
            xAxis.setLabelCount(entries.size(), true);

            // Đặt phạm vi trục X dựa trên dữ liệu
            xAxis.setAxisMinimum(entries.get(0).getX());
            xAxis.setAxisMaximum(entries.get(entries.size() - 1).getX());

            // Cấu hình trục Y
            YAxis leftAxis = lineChart.getAxisLeft();
            leftAxis.setAxisMinimum(0f);
            lineChart.getAxisRight().setEnabled(false);

            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.getDescription().setEnabled(false);
            lineChart.invalidate();
        }

        // Phần RecyclerView
        Collections.sort(records, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        HealthRecordAdapter adapter = new HealthRecordAdapter(records);
        recyclerView.setAdapter(adapter);
    }
}