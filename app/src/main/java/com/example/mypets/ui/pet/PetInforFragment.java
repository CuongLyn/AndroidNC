package com.example.mypets.ui.pet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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

import androidx.annotation.NonNull;
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
    private Button btnChonGio, btnVaccination;

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

        ImageView img1 = rootView.findViewById(R.id.img1);
        ImageView img2 = rootView.findViewById(R.id.img2);
        ImageView img3 = rootView.findViewById(R.id.img3);

        img1.setImageResource(R.drawable.meo);
        img2.setImageResource(R.drawable.meo);
        img3.setImageResource(R.drawable.meo);

        // Spinner bữa ăn
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

            // Xử lý nút xem danh sách tiêm phòng
            btnVaccination.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("petId", petId);
                NavController navController = Navigation.findNavController(rootView);
                navController.navigate(R.id.action_petInforFragment_to_vaccinationListFragment, args);
            });

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

        // Khi chọn Spinner
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

        // Hiện TimePicker khi bấm vào EditText
        etGioAnTheoBuoi.setOnClickListener(v -> {
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
                        setAlarm(hourOfDay, minute1, buoi);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        return rootView;
    }

    private void updatePetFeedTime(String buoi, String time) {
        if (petRef != null) {
            petRef.child("gioAn").child(buoi.toLowerCase()).setValue(time);
        }
    }

    private void setAlarm(int hour, int minute, String buoi) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("buoi", buoi);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), buoi.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }
}
