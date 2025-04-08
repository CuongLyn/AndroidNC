package com.example.mypets.ui.pet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypets.R;
import com.example.mypets.data.model.Pet;

public class PetInforFragment extends Fragment {

    private Pet currentPet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pet_infor, container, false);

        // Nhận dữ liệu pet
        if (getArguments() != null) {
            currentPet = (Pet) getArguments().getSerializable("pet");
        }
        // Xử lý nút Tiêm phòng
        Button btnVaccination = view.findViewById(R.id.btn_tiemphong);
        btnVaccination.setOnClickListener(v -> {
            if (currentPet != null) {
                Bundle args = new Bundle();
                args.putString("petId", currentPet.getId()); // Truyền petId

                // Điều hướng bằng Navigation Component
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_petInforFragment_to_vaccinationListFragment, args);
            }
        });

        // Xử lý nút Theo dõi sức khỏe
        Button btnHealthRecord = view.findViewById(R.id.btn_dexuat);
        btnHealthRecord.setOnClickListener(v -> {
            if (currentPet != null) {
                Bundle args = new Bundle();
                args.putString("petId", currentPet.getId()); // Truyền petId

                // Điều hướng đến PetHealthFragment
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_petInforFragment_to_petHealthFragment, args);
            }
        });



        // Ánh xạ view
        TextView tvTen = view.findViewById(R.id.tvTen);
        TextView tvLoai = view.findViewById(R.id.tvLoai);
        TextView tvTuoi = view.findViewById(R.id.tvTuoi);
        TextView tvGioiTinh = view.findViewById(R.id.tvGioiTinh);
        ImageView img1 = view.findViewById(R.id.img1);
        ImageView img2 = view.findViewById(R.id.img2);
        ImageView img3 = view.findViewById(R.id.img3);

        // Hiển thị thông tin
        if (currentPet != null) {
            tvTen.setText(currentPet.getName());
            tvLoai.setText(currentPet.getLoai());
            tvTuoi.setText(String.valueOf(currentPet.getTuoi()));
            tvGioiTinh.setText(currentPet.getGioiTinh());
            img1.setImageResource(R.drawable.meo);
            img2.setImageResource(R.drawable.meo);
            img3.setImageResource(R.drawable.meo);


        }

        return view;
    }
}