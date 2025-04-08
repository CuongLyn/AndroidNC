package com.example.mypets.ui.pet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mypets.BroadcastReceiver.AlarmReceiver;
import com.example.mypets.R;
import com.example.mypets.data.model.Pet.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PetInforFragment extends Fragment {

    private TextView tvName, tvLoai, tvTuoi, tvGioiTinh, tvLichTiem, tvLichKiemTra;
    private EditText etGioAnTheoBuoi;
    private Spinner spBuoiAn;
    private Button btnChonGio;

    private FirebaseDatabase database;
    private DatabaseReference petRef;

    private String gioSang = "", gioTrua = "", gioToi = "";

    MediaPlayer mediaPlayer;

//    //Sensor
//    private SensorManager sensorManager;
//    private Sensor gyroscope;
//    private long lastShakeTime = 0;
//    private static final int SHAKE_THRESHOLD = 3; // có thể tinh chỉnh
//    private int currentPetIndex = 0;
//    private List<String> petIdList = new ArrayList<>();


    public PetInforFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pet_infor, container, false);

        tvName = rootView.findViewById(R.id.tvName);
        tvLoai = rootView.findViewById(R.id.tvLoai);
        tvTuoi = rootView.findViewById(R.id.tvTuoi);
        tvGioiTinh = rootView.findViewById(R.id.tvGioiTinh);
        tvLichTiem = rootView.findViewById(R.id.tvLichTiem);
        tvLichKiemTra = rootView.findViewById(R.id.tvLichKiemTra);

        etGioAnTheoBuoi = rootView.findViewById(R.id.etGioAnTheoBuoi);
        spBuoiAn = rootView.findViewById(R.id.spBuoiAn);




        String[] buoiAn = {"Sáng", "Trưa", "Tối"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, buoiAn);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBuoiAn.setAdapter(adapter);

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
                    if (pet != null) {
                        tvName.setText("Tên: " + pet.getName());
                        tvLoai.setText("Loài: " + pet.getLoai());
                        tvTuoi.setText("Tuổi: " + pet.getTuoi());
                        tvGioiTinh.setText("Giới tính: " + pet.getGioiTinh());
                        tvLichTiem.setText(pet.getLichTiem());
                        tvLichKiemTra.setText(pet.getLichKiemTraSucKhoe());

                        // Lấy giờ ăn nếu có
                        DataSnapshot gioAnSnapshot = dataSnapshot.child("gioAn");
                        gioSang = gioAnSnapshot.child("sang").getValue(String.class) != null ? gioAnSnapshot.child("sang").getValue(String.class) : "";
                        gioTrua = gioAnSnapshot.child("trua").getValue(String.class) != null ? gioAnSnapshot.child("trua").getValue(String.class) : "";
                        gioToi = gioAnSnapshot.child("toi").getValue(String.class) != null ? gioAnSnapshot.child("toi").getValue(String.class) : "";

                        int selectedPosition = spBuoiAn.getSelectedItemPosition();
                        switch (selectedPosition) {
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
                    Log.e("PetInforFragment", "Error fetching data from Firebase: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("PetInforFragment", "Bundle is null!");
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



        etGioAnTheoBuoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        (view1, hourOfDay, minute1) -> {
                            String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                            etGioAnTheoBuoi.setText(selectedTime);

                            String buoi = spBuoiAn.getSelectedItem().toString();
                            switch (buoi) {
                                case "Sáng":
                                    gioSang = selectedTime;
                                    break;
                                case "Trưa":
                                    gioTrua = selectedTime;
                                    break;
                                case "Tối":
                                    gioToi = selectedTime;
                                    break;
                            }

                            updatePetFeedTime(buoi, selectedTime);

                            //
                            setAlarm(hourOfDay, minute1, buoi);



                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

//        //khoi tao cam bien
//        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
//        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//
//        if (gyroscope != null) {
//            sensorManager.registerListener(gyroListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
//        }

//        //Tai danh sach petId
//        DatabaseReference petsRef = FirebaseDatabase.getInstance().getReference("pets");
//        petsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                petIdList.clear();
//                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
//                    petIdList.add(petSnapshot.getKey());
//                }
//
//                String currentId = getArguments().getString("id");
//                currentPetIndex = petIdList.indexOf(currentId);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });




        return rootView;
    }

//    //dinh nghia sensorEventListener
//    private final SensorEventListener gyroListener = new SensorEventListener() {
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            float rotX = event.values[0];
//            float rotY = event.values[1];
//            float rotZ = event.values[2];
//
//            float rotationMagnitude = (float) Math.sqrt(rotX * rotX + rotY * rotY + rotZ * rotZ);
//
//            if (rotationMagnitude > SHAKE_THRESHOLD) {
//                long currentTime = System.currentTimeMillis();
//                if (currentTime - lastShakeTime > 1000) { // giới hạn thời gian để tránh lặp nhanh
//                    lastShakeTime = currentTime;
//                    nextPet();
//                }
//            }
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
//    };

//    private void nextPet() {
//        if (petIdList.isEmpty()) return;
//
//        currentPetIndex = (currentPetIndex + 1) % petIdList.size();
//        String nextPetId = petIdList.get(currentPetIndex);
//
//        Bundle bundle = new Bundle();
//        bundle.putString("id", nextPetId);
//
//        PetInforFragment newFragment = new PetInforFragment();
//        newFragment.setArguments(bundle);
//
//        requireActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.nav_host_fragment_content_main, newFragment) // ID container fragment của bạn
//                .addToBackStack(null)
//                .commit();
//    }



    private void updatePetFeedTime(String buoi, String gio) {
        if (petRef != null) {
            DatabaseReference gioAnRef = petRef.child("gioAn");
            switch (buoi) {
                case "Sáng":
                    gioAnRef.child("sang").setValue(gio);
                    break;
                case "Trưa":
                    gioAnRef.child("trua").setValue(gio);
                    break;
                case "Tối":
                    gioAnRef.child("toi").setValue(gio);
                    break;
            }
            Log.d("PetInforFragment", "Cập nhật giờ ăn " + buoi + ": " + gio);
        } else {
            Log.e("PetInforFragment", "petRef is null, không thể cập nhật giờ ăn");
        }
    }

    private void setAlarm(int hour, int minute, String buoi) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            // Nếu giờ đã qua hôm nay thì đặt cho ngày mai
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("buoi", buoi);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(), buoi.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("PetInforFragment", "Đặt báo thức lúc " + hour + ":" + minute + " cho " + buoi);
        }
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        sensorManager.unregisterListener(gyroListener);
//    }



}
