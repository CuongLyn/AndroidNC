package com.example.mypets.ui.pet;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.R;
import com.example.mypets.adapter.ImagePreviewAdapter;
import com.example.mypets.data.model.Pet.Pet;
import com.google.android.gms.common.util.IOUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddPetFragment extends Fragment {

    private EditText nameEditText, loaiEditText, tuoiEditText, lichTiemEditText, lichKiemTraSucKhoeEditText;
    private RadioGroup gioiTinhRadioGroup;
    private Button btnChooseImages;
    private List<Uri> selectedImageUris = new ArrayList<>();

    private ImagePreviewAdapter adapter;

    private static final int PICK_IMAGES_REQUEST = 1;
    private Button addButton;
    private DatabaseReference mDatabase;

    public AddPetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_pet, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("pets");

        nameEditText = view.findViewById(R.id.editTextName);
        loaiEditText = view.findViewById(R.id.editTextLoai);
        tuoiEditText = view.findViewById(R.id.editTextTuoi);
        lichTiemEditText = view.findViewById(R.id.editTextLichTiem);
        lichKiemTraSucKhoeEditText = view.findViewById(R.id.editTextLichKiemTra);
        gioiTinhRadioGroup = view.findViewById(R.id.radioGroupGioiTinh);
        addButton = new Button(getContext());
        addButton.setText("Thêm");
        ((ViewGroup) view).addView(addButton); // Thêm nút nếu XML chưa có

        addButton.setOnClickListener(v -> addPet());

        lichTiemEditText.setOnClickListener(v -> showDatePickerDialog(lichTiemEditText));
        lichKiemTraSucKhoeEditText.setOnClickListener(v -> showDatePickerDialog(lichKiemTraSucKhoeEditText));

        btnChooseImages = view.findViewById(R.id.btnChooseImages);

        RecyclerView rvImagePreview = view.findViewById(R.id.rvImagePreview);
        rvImagePreview.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ImagePreviewAdapter(selectedImageUris);
        rvImagePreview.setAdapter(adapter);

        btnChooseImages.setOnClickListener(v -> openImagePicker());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGES_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    selectedImageUris.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                selectedImageUris.add(data.getData());
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void addPet() {
        // Lấy dữ liệu từ các trường nhập liệu
        String name = nameEditText.getText().toString().trim();
        String loai = loaiEditText.getText().toString().trim();
        String tuoiStr = tuoiEditText.getText().toString().trim();
        String lichTiem = lichTiemEditText.getText().toString().trim();
        String lichKiemTraSucKhoe = lichKiemTraSucKhoeEditText.getText().toString().trim();
        String ownerId = "a67a506e0b17"; // ID chủ sở hữu cố định

        // Validate dữ liệu nhập
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Vui lòng nhập tên thú cưng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(loai)) {
            Toast.makeText(getContext(), "Vui lòng nhập loài", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(tuoiStr)) {
            Toast.makeText(getContext(), "Vui lòng nhập tuổi", Toast.LENGTH_SHORT).show();
            return;
        }

        int tuoi;
        try {
            tuoi = Integer.parseInt(tuoiStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Tuổi phải là số nguyên", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedGenderId = gioiTinhRadioGroup.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(getContext(), "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUris.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn ít nhất 1 ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedGenderButton = getView().findViewById(selectedGenderId);
        String gioiTinh = selectedGenderButton.getText().toString();

        // Hiển thị loading
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tải lên " + selectedImageUris.size() + " ảnh...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Xử lý upload ảnh trong luồng background
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                List<String> uploadedImageUrls = new ArrayList<>();

                // Upload từng ảnh
                for (Uri imageUri : selectedImageUris) {
                    InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
                    byte[] imageData = IOUtils.toByteArray(inputStream);

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(
                                    "image",
                                    "pet_image.jpg",
                                    RequestBody.create(MediaType.parse("image/*"), imageData))
                                            .build();

                    Request request = new Request.Builder()
                            .url("https://api.imgur.com/3/image")
                            .header("Authorization", "Client-ID bf3e80c62db9f4b")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseBody = response.body().string();

                    JSONObject json = new JSONObject(responseBody);
                    if (json.getBoolean("success")) {
                        String imageUrl = json.getJSONObject("data").getString("link");
                        uploadedImageUrls.add(imageUrl);
                    }
                }

                // Xử lý kết quả trên UI thread
                requireActivity().runOnUiThread(() -> {
                    progressDialog.dismiss();

                    if (uploadedImageUrls.isEmpty()) {
                        Toast.makeText(getContext(), "Lỗi: Không thể tải lên ảnh", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Tạo và lưu thú cưng
                    String petId = mDatabase.push().getKey();
                    Pet newPet = new Pet(
                            petId,
                            name,
                            loai,
                            tuoi,
                            gioiTinh,
                            lichTiem,
                            lichKiemTraSucKhoe,
                            ownerId,
                            uploadedImageUrls
                    );

                    mDatabase.child(petId).setValue(newPet)
                            .addOnSuccessListener(aVoid -> {
                                // Thông báo thành công
                                Toast.makeText(getContext(),"Đã thêm " + name + " thành công!\n", Toast.LENGTH_LONG).show();
                                // Reset form
                                resetForm();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(),
                                        "Lỗi lưu dữ liệu: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),
                            "Lỗi: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    // Hàm reset form
    private void resetForm() {
        nameEditText.setText("");
        loaiEditText.setText("");
        tuoiEditText.setText("");
        lichTiemEditText.setText("");
        lichKiemTraSucKhoeEditText.setText("");
        gioiTinhRadioGroup.clearCheck();
        selectedImageUris.clear();
        adapter.notifyDataSetChanged();
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editText.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }




}
