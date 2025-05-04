package com.example.mypets.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mypets.MainActivity;
import com.example.mypets.R;
import com.example.mypets.data.model.Clinic;
import com.example.mypets.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private RadioGroup radioGroupRole;
    private RadioButton radioUser, radioClinic;
    private EditText etEmail, etPassword, etClinicName, etClinicAddress,
            etClinicPhone, etClinicLat, etClinicLng, etClinicServices, etWorkingHours;
    private LinearLayout clinicFields;
    private Button btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, clinicsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("user");
        clinicsRef = FirebaseDatabase.getInstance().getReference("clinics");

        // Initialize Views
        radioGroupRole = findViewById(R.id.radioGroupRole);
        radioUser = findViewById(R.id.radioUser);
        radioClinic = findViewById(R.id.radioClinic);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        clinicFields = findViewById(R.id.clinicFields);
        etClinicName = findViewById(R.id.etClinicName);
        etClinicAddress = findViewById(R.id.etClinicAddress);
        etClinicPhone = findViewById(R.id.etClinicPhone);
        etClinicLat = findViewById(R.id.etClinicLat);
        etClinicLng = findViewById(R.id.etClinicLng);
        etClinicServices = findViewById(R.id.etClinicServices);
        etWorkingHours = findViewById(R.id.etWorkingHours);
        btnRegister = findViewById(R.id.btnRegister);

        // RadioGroup listener
        radioGroupRole.setOnCheckedChangeListener((group, checkedId) -> {
            clinicFields.setVisibility(checkedId == R.id.radioClinic ? View.VISIBLE : View.GONE);
        });

        // Register Button Click
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = radioUser.isChecked() ? "user" : "clinic";

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Vui lòng nhập email và mật khẩu");
            return;
        }

        if (role.equals("clinic")) {
            handleClinicRegistration();
        } else {
            createFirebaseUser(email, password, "user", null);
        }
    }

    private void handleClinicRegistration() {
        try {
            String name = etClinicName.getText().toString().trim();
            String address = etClinicAddress.getText().toString().trim();
            String phone = etClinicPhone.getText().toString().trim();
            double lat = Double.parseDouble(etClinicLat.getText().toString());
            double lng = Double.parseDouble(etClinicLng.getText().toString());
            List<String> services = Arrays.asList(etClinicServices.getText().toString().split(","));
            String workingHours = etWorkingHours.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                showToast("Vui lòng nhập đủ thông tin bắt buộc");
                return;
            }

            Clinic clinic = new Clinic(
                    address,
                    0.0, // Default distance
                    lat,
                    lng,
                    name,
                    phone,
                    services,
                    workingHours
            );

            createFirebaseUser(etEmail.getText().toString().trim(),
                    etPassword.getText().toString().trim(),
                    "clinic",
                    clinic);

        } catch (NumberFormatException e) {
            showToast("Vĩ độ/kinh độ không hợp lệ");
        }
    }

    private void createFirebaseUser(String email, String password, String role, Clinic clinic) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();

                        // Tạo đối tượng User
                        User user = new User();
                        user.setUid(userId);
                        user.setEmail(email);
                        user.setRole(role);

                        // Thêm thông tin phòng khám nếu có
                        if (role.equals("clinic")) {
                            user.setName(etClinicName.getText().toString().trim());
                            user.setPhone(etClinicPhone.getText().toString().trim());
                        }

                        // Lưu toàn bộ thông tin user
                        usersRef.child(userId).setValue(user);

                        // Xử lý thông tin phòng khám
                        if (clinic != null) {
                            String clinicId = clinicsRef.push().getKey();
                            clinicsRef.child(clinicId).setValue(clinic)
                                    .addOnSuccessListener(aVoid -> {
                                        usersRef.child(userId).child("clinicId").setValue(clinicId);
                                        redirectToMain(role);
                                    });
                        } else {
                            redirectToMain(role);
                        }
                    } else {
                        showToast("Đăng ký thất bại: " + task.getException().getMessage());
                    }
                });
    }

    private void redirectToMain(String role) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER_ROLE", role);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}