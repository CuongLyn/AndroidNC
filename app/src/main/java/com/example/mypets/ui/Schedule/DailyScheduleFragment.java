package com.example.mypets.ui.Schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mypets.R;
import com.example.mypets.adapter.ScheduleAdapter;
import com.example.mypets.data.model.Pet.Pet;
import com.example.mypets.data.model.Schedule;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DailyScheduleFragment extends Fragment implements ScheduleAdapter.OnScheduleActionListener {

    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private List<Schedule> schedules = new ArrayList<>();
    private DatabaseReference databaseRef;
    private String userId = "a67a506e0b17";
    private List<Pet> pets = new ArrayList<>();

    // New date handling components
    private TextView tvCurrentDate;
    private com.google.android.material.button.MaterialButton btnPreviousDay, btnNextDay;
    private Calendar currentDate = Calendar.getInstance();
    private Calendar selectedDateTime = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_schedule, container, false);

        // Initialize date controls
        tvCurrentDate = view.findViewById(R.id.tv_current_date);
        btnPreviousDay = view.findViewById(R.id.btn_previous_day);
        btnNextDay = view.findViewById(R.id.btn_next_day);

        // Setup date change listeners
        btnPreviousDay.setOnClickListener(v -> changeDate(-1));
        btnNextDay.setOnClickListener(v -> changeDate(1));

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_schedules);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScheduleAdapter(schedules, getContext());
        adapter.setOnScheduleActionListener(this);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("userSchedules").child(userId);

        // Load initial data
        updateDateDisplay();
        loadPets();

        // Setup FAB
        view.findViewById(R.id.fab_add_schedule).setOnClickListener(v -> showAddDialog());

        btnPreviousDay = view.findViewById(R.id.btn_previous_day);
        btnNextDay = view.findViewById(R.id.btn_next_day);

        // Xử lý sự kiện click
        btnPreviousDay.setOnClickListener(v -> changeDate(-1)); // Giảm 1 ngày
        btnNextDay.setOnClickListener(v -> changeDate(1));      // Tăng 1 ngày

        return view;
    }

    private void loadPets() {
        FirebaseDatabase.getInstance().getReference("pets")
                .orderByChild("ownerId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pets.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Pet pet = data.getValue(Pet.class);
                            if (pet != null) {
                                pet.setId(data.getKey());
                                pets.add(pet);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Error loading pets: " + error.getMessage());
                    }
                });
    }
    private void changeDate(int daysToAdd) {
        currentDate.add(Calendar.DAY_OF_MONTH, daysToAdd);
        updateDateDisplay();
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvCurrentDate.setText(sdf.format(currentDate.getTime()));
        loadSchedulesForSelectedDate();
    }

    private void loadSchedulesForSelectedDate() {
        // Tính toán khoảng thời gian trong ngày
        Calendar startOfDay = (Calendar) currentDate.clone();
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);

        Calendar endOfDay = (Calendar) currentDate.clone();
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);

        // Truy vấn Firebase
        databaseRef.orderByChild("time")
                .startAt(startOfDay.getTimeInMillis())
                .endAt(endOfDay.getTimeInMillis())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        schedules.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Schedule schedule = data.getValue(Schedule.class);
                            if (schedule != null) {
                                schedule.setScheduleId(data.getKey());
                                // Tìm tên thú cưng
                                for (Pet pet : pets) {
                                    if (pet.getId().equals(schedule.getPetId())) {
                                        schedule.setPetName(pet.getName());
                                        break;
                                    }
                                }
                                schedules.add(schedule);
                            }
                        }
                        // Sắp xếp theo thời gian
                        Collections.sort(schedules, (s1, s2) ->
                                Long.compare(s1.getTime(), s2.getTime()));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Lỗi tải lịch trình", error.toException());
                    }
                });
    }

    private void showAddDialog() {
        if (pets.isEmpty()) {
            Toast.makeText(getContext(), "Bạn chưa có thú cưng", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_schedule, null);
        builder.setView(dialogView);

        // Initialize views
        Spinner spinner = dialogView.findViewById(R.id.spinner_pets);
        TextInputEditText etDate = dialogView.findViewById(R.id.et_date);
        TextInputEditText etTime = dialogView.findViewById(R.id.et_time);
        TextInputEditText etActivity = dialogView.findViewById(R.id.et_activity);
        TextInputEditText etNote = dialogView.findViewById(R.id.et_note);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSave = dialogView.findViewById(R.id.btn_save);

        // Setup spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getPetNames()
        );
        spinner.setAdapter(spinnerAdapter);

        // Date picker
        etDate.setOnClickListener(v -> showDatePicker(etDate));

        // Time picker
        etTime.setOnClickListener(v -> showTimePicker(etTime));

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> handleSaveSchedule(
                dialog,
                spinner.getSelectedItemPosition(),
                etActivity.getText().toString().trim(),
                etNote.getText().toString().trim(),
                etDate,
                etTime
        ));
    }

    private void handleSaveSchedule(
            AlertDialog dialog,
            int selectedPosition,
            String activity,
            String note,
            TextInputEditText etDate,
            TextInputEditText etTime
    ) {
        // Validation
        if (selectedPosition == AdapterView.INVALID_POSITION) {
            Toast.makeText(getContext(), "Vui lòng chọn thú cưng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (activity.isEmpty()) {
            ((TextInputEditText) dialog.findViewById(R.id.et_activity))
                    .setError("Vui lòng nhập hoạt động");
            return;
        }

        if (etDate.getText().toString().isEmpty()) {
            etDate.setError("Vui lòng chọn ngày");
            return;
        }

        if (etTime.getText().toString().isEmpty()) {
            etTime.setError("Vui lòng chọn giờ");
            return;
        }

        // Create new schedule
        Pet selectedPet = pets.get(selectedPosition);
        Schedule newSchedule = new Schedule(
                selectedPet.getId(),
                activity,
                selectedDateTime.getTimeInMillis(),
                note
        );
        newSchedule.setPetName(selectedPet.getName());

        // Save to Firebase
        databaseRef.push().setValue(newSchedule)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onEditClick(Schedule schedule) {
        showEditDialog(schedule);
    }

    @Override
    public void onDeleteClick(Schedule schedule) {
        showDeleteConfirmationDialog(schedule);
    }

    private void showDeleteConfirmationDialog(Schedule schedule) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa lịch trình")
                .setMessage("Bạn chắc chắn muốn xóa?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteSchedule(schedule))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteSchedule(Schedule schedule) {
        if (schedule.getScheduleId() == null) return;

        databaseRef.child(schedule.getScheduleId()).removeValue()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showEditDialog(Schedule schedule) {
        if (pets.isEmpty()) {
            Toast.makeText(getContext(), "Không có thú cưng để chỉnh sửa", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_schedule, null);
        builder.setView(dialogView);

        // Initialize views
        Spinner spinner = dialogView.findViewById(R.id.spinner_pets);
        TextInputEditText etDate = dialogView.findViewById(R.id.et_date);
        TextInputEditText etTime = dialogView.findViewById(R.id.et_time);
        TextInputEditText etActivity = dialogView.findViewById(R.id.et_activity);
        TextInputEditText etNote = dialogView.findViewById(R.id.et_note);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSave = dialogView.findViewById(R.id.btn_save);

        // Pre-fill data
        selectedDateTime.setTimeInMillis(schedule.getTime());
        etDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDateTime.getTime()));
        etTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedDateTime.getTime()));
        etActivity.setText(schedule.getActivity());
        etNote.setText(schedule.getNote());

        // Setup spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getPetNames()
        );
        spinner.setAdapter(spinnerAdapter);

        // Select current pet
        for (int i = 0; i < pets.size(); i++) {
            if (pets.get(i).getId().equals(schedule.getPetId())) {
                spinner.setSelection(i);
                break;
            }
        }

        // Date/Time pickers
        etDate.setOnClickListener(v -> showDatePicker(etDate));
        etTime.setOnClickListener(v -> showTimePicker(etTime));

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            // Validation
            int selectedPosition = spinner.getSelectedItemPosition();
            String activity = etActivity.getText().toString().trim();
            String note = etNote.getText().toString().trim();

            if (selectedPosition == AdapterView.INVALID_POSITION) {
                Toast.makeText(getContext(), "Vui lòng chọn thú cưng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (activity.isEmpty()) {
                etActivity.setError("Vui lòng nhập hoạt động");
                return;
            }

            if (etDate.getText().toString().isEmpty()) {
                etDate.setError("Vui lòng chọn ngày");
                return;
            }

            if (etTime.getText().toString().isEmpty()) {
                etTime.setError("Vui lòng chọn giờ");
                return;
            }

            // Update schedule
            Pet selectedPet = pets.get(selectedPosition);
            schedule.setPetId(selectedPet.getId());
            schedule.setPetName(selectedPet.getName());
            schedule.setActivity(activity);
            schedule.setTime(selectedDateTime.getTimeInMillis());
            schedule.setNote(note);

            databaseRef.child(schedule.getScheduleId()).setValue(schedule)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private List<String> getPetNames() {
        List<String> names = new ArrayList<>();
        for (Pet pet : pets) names.add(pet.getName());
        return names;
    }

    private void showDatePicker(TextInputEditText etDate) {
        new DatePickerDialog(
                requireContext(),
                (view, year, month, day) -> {
                    // Sửa thành selectedDateTime
                    selectedDateTime.set(year, month, day);
                    etDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(selectedDateTime.getTime()));
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showTimePicker(TextInputEditText etTime) {
        new TimePickerDialog(
                requireContext(),
                (view, hour, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hour);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    etTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault())
                            .format(selectedDateTime.getTime()));
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true
        ).show();
    }

}