package com.example.mypets.ui.clinics;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.widget.Toast;
import com.example.mypets.R;
import com.example.mypets.adapter.ClinicAdapter;
import com.example.mypets.data.model.Clinic;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapFragment extends Fragment {
    private FusedLocationProviderClient fusedLocationClient;
    private RecyclerView rvClinics;
    private ClinicAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        rvClinics = view.findViewById(R.id.rvClinics);
        rvClinics.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        checkLocationPermission();
    }

    // Kiểm tra và yêu cầu quyền truy cập vị trí
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(requireContext(), "Ứng dụng cần quyền truy cập vị trí", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double userLat = location.getLatitude();
                    double userLng = location.getLongitude();
                    loadClinics(userLat, userLng); // Truyền tọa độ người dùng
                } else {
                    Log.e("Location", "Không thể lấy vị trí");
                }
            });
        }
    }

    // Tải danh sách Clinic và lọc theo khoảng cách ≤10km
    private void loadClinics(double userLat, double userLng) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("clinics");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Clinic> clinics = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Clinic clinic = ds.getValue(Clinic.class);
                    if (clinic != null) {
                        // Tính khoảng cách và kiểm tra
                        double distance = calculateDistance(userLat, userLng, clinic.getLat(), clinic.getLng());
                        clinic.setDistance(distance);

                        if (distance <= 10) { // Chỉ thêm Clinic trong phạm vi 10km
                            clinics.add(clinic);
                        }
                    }
                }
                Collections.sort(clinics, (c1, c2) -> Double.compare(c1.getDistance(), c2.getDistance()));
                updateUI(clinics);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi tải dữ liệu", error.toException());
            }
        });
    }

    // Tính khoảng cách (km)
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, results);
        return results[0] / 1000;
    }


    private void updateUI(List<Clinic> clinics) {
        if (clinics.isEmpty()) {
            Toast.makeText(getContext(), "Không có phòng khám nào trong phạm vi 10km", Toast.LENGTH_SHORT).show();
        }
        adapter = new ClinicAdapter(clinics, clinic -> {
            ClinicDetailBottomSheet bottomSheet = ClinicDetailBottomSheet.newInstance(clinic);
            bottomSheet.show(getParentFragmentManager(), "ClinicDetail");
        });
        rvClinics.setAdapter(adapter);
    }
}