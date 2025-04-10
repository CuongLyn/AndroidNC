package com.example.mypets.ui.pet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mypets.BroadcastReceiver.AlarmReceiver;
import com.example.mypets.R;
import com.example.mypets.data.model.Pet.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class PetInforFragment extends Fragment {

    private TextView tvName, tvLoai, tvTuoi, tvGioiTinh, tvLichTiem, tvLichKiemTra;
    private EditText etGioAnTheoBuoi;
    private Spinner spBuoiAn;
    private Button btnVaccination;
    private Button btnHoSoKhamBenh;
    private FirebaseDatabase database;
    private DatabaseReference petRef;
    private String gioSang = "", gioTrua = "", gioToi = "";
    private Pet currentPet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pet_infor, container, false);

        // Ánh xạ view
        tvName = rootView.findViewById(R.id.tvName);
        tvLoai = rootView.findViewById(R.id.tvLoai);
        tvTuoi = rootView.findViewById(R.id.tvTuoi);
        tvGioiTinh = rootView.findViewById(R.id.tvGioiTinh);
        tvLichTiem = rootView.findViewById(R.id.tvLichTiem);
        tvLichKiemTra = rootView.findViewById(R.id.tvLichKiemTra);
        etGioAnTheoBuoi = rootView.findViewById(R.id.etGioAnTheoBuoi);
        spBuoiAn = rootView.findViewById(R.id.spBuoiAn);
        btnVaccination = rootView.findViewById(R.id.btn_tiemphong);
        btnHoSoKhamBenh = rootView.findViewById(R.id.btn_hoso);

        // Nhận dữ liệu pet
        if (getArguments() != null) {
            currentPet = (Pet) getArguments().getSerializable("pet");
        }

        //Xu ly nut ho so kham benh
        btnHoSoKhamBenh.setOnClickListener(v -> {
            if (currentPet != null) {
                Bundle args = new Bundle();
                args.putString("petId", currentPet.getId());
                NavController navController = Navigation.findNavController(rootView);
                navController.navigate(R.id.action_petInforFragment_to_medicalRecordFragment, args);
            }
        });

        // Xử lý nút Tiêm phòng
        btnVaccination.setOnClickListener(v -> {
            if (currentPet != null) {
                Bundle args = new Bundle();
                args.putString("petId", currentPet.getId());
                NavController navController = Navigation.findNavController(rootView);
                navController.navigate(R.id.action_petInforFragment_to_vaccinationListFragment, args);
            }
        });

        // Xử lý nút Theo dõi sức khỏe
        Button btnHealthRecord = rootView.findViewById(R.id.btn_dexuat);
        btnHealthRecord.setOnClickListener(v -> {
            if (currentPet != null) {
                Bundle args = new Bundle();
                args.putString("petId", currentPet.getId());
                NavController navController = Navigation.findNavController(rootView);
                navController.navigate(R.id.action_petInforFragment_to_petHealthFragment, args);
            }
        });



        // Spinner bữa ăn
        String[] buoiAn = {"Sáng", "Trưa", "Tối"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, buoiAn);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBuoiAn.setAdapter(adapter);

        // Load dữ liệu từ Firebase
        Bundle bundle = getArguments();
        if (bundle != null) {
            String petId = bundle.getString("id");
            if (petId == null || petId.isEmpty()) {
                Log.e("PetInforFragment", "petId is null or empty!");
                return rootView;
            }

            database = FirebaseDatabase.getInstance();
            petRef = database.getReference("pets").child(petId);

            petRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Pet pet = dataSnapshot.getValue(Pet.class);
                    currentPet = pet;
                    if (pet != null) {
                        tvName.setText("Tên: " + pet.getName());
                        tvLoai.setText("Loài: " + pet.getLoai());
                        tvTuoi.setText("Tuổi: " + pet.getTuoi());
                        tvGioiTinh.setText("Giới tính: " + pet.getGioiTinh());
                        tvLichTiem.setText(pet.getLichTiem());
                        tvLichKiemTra.setText(pet.getLichKiemTraSucKhoe());

                        DataSnapshot gioAnSnapshot = dataSnapshot.child("gioAn");
                        gioSang = gioAnSnapshot.child("sang").getValue(String.class) != null ? gioAnSnapshot.child("sang").getValue(String.class) : "";
                        gioTrua = gioAnSnapshot.child("trua").getValue(String.class) != null ? gioAnSnapshot.child("trua").getValue(String.class) : "";
                        gioToi = gioAnSnapshot.child("toi").getValue(String.class) != null ? gioAnSnapshot.child("toi").getValue(String.class) : "";

                        switch (spBuoiAn.getSelectedItemPosition()) {
                            case 0:
                                etGioAnTheoBuoi.setText(gioSang);
                                break;
                            case 1:
                                etGioAnTheoBuoi.setText(gioTrua);
                                break;
                            case 2:
                                etGioAnTheoBuoi.setText(gioToi);
                                break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("PetInforFragment", "Firebase error: " + databaseError.getMessage());
                }
            });
        }

        // Xử lý chọn Spinner
        spBuoiAn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        etGioAnTheoBuoi.setText(gioSang);
                        break;
                    case 1:
                        etGioAnTheoBuoi.setText(gioTrua);
                        break;
                    case 2:
                        etGioAnTheoBuoi.setText(gioToi);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Xử lý chọn giờ
        etGioAnTheoBuoi.setOnClickListener(v -> showTimePickerDialog());

        return rootView;
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute1) -> {
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                    etGioAnTheoBuoi.setText(selectedTime);
                    String buoi = spBuoiAn.getSelectedItem().toString();
                    updatePetFeedTime(buoi, selectedTime);
                    setAlarm(hourOfDay, minute1, buoi);
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

    // Cập nhật thời gian ăn vào Firebase
    private void updatePetFeedTime(String mealTime, String selectedTime) {
        if (currentPet == null || currentPet.getId() == null) return;

        DatabaseReference gioAnRef = FirebaseDatabase.getInstance().getReference("pets").child(currentPet.getId()).child("gioAn");
        switch (mealTime) {
            case "Sáng":
                gioAnRef.child("sang").setValue(selectedTime);
                break;
            case "Trưa":
                gioAnRef.child("trua").setValue(selectedTime);
                break;
            case "Tối":
                gioAnRef.child("toi").setValue(selectedTime);
                break;
        }
    }

    // Đặt báo thức
    private void setAlarm(int hour, int minute, String mealTime) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("MEAL_TIME", mealTime);

        int requestCode = mealTime.hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        try {
            // Kiểm tra quyền SCHEDULE_EXACT_ALARM (cho Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    // Yêu cầu người dùng cấp quyền
                    Intent settingsIntent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(settingsIntent);
                    Toast.makeText(requireContext(), "Vui lòng cấp quyền đặt báo thức chính xác", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // Đặt báo thức
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

            Toast.makeText(requireContext(), "Đã đặt báo thức cho " + mealTime, Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Log.e("AlarmError", "Không có quyền đặt báo thức: " + e.getMessage());
            Toast.makeText(requireContext(), "Lỗi: Không có quyền đặt báo thức", Toast.LENGTH_SHORT).show();
        }
    }
}