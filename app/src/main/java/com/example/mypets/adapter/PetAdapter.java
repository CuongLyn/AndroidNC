package com.example.mypets.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.R;
import com.example.mypets.data.model.Pet;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private List<Pet> petList;
    private Context context;

    public PetAdapter(Context context, List<Pet> petList) {
        this.context = context;
        this.petList = petList;
    }

    @Override
    public PetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }



    @Override
    public void onBindViewHolder(PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.name.setText(pet.getName());
        holder.loai.setText(pet.getLoai());

        // Xử lý khi nhấn nút xóa
        holder.itemView.findViewById(R.id.deleteIcon).setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa thú cưng")
                    .setMessage("Bạn có chắc muốn xóa thú cưng này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        deletePetFromFirebase(pet.getId(), holder.getAdapterPosition());
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        holder.itemView.findViewById(R.id.editIcon).setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(pet);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(pet);
            }
        });

    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public class PetViewHolder extends RecyclerView.ViewHolder {
        TextView name, loai;

        public PetViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewName);
            loai = itemView.findViewById(R.id.textViewLoai);
        }
    }

    //delete
    private void deletePetFromFirebase(String petId, int position) {
        DatabaseReference petRef = FirebaseDatabase.getInstance()
                .getReference("pets")
                .child(petId);

        petRef.removeValue().addOnSuccessListener(aVoid -> {
            if (position >= 0 && position < petList.size()) {
                petList.remove(position);
                notifyItemRemoved(position);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Lỗi khi xóa thú cưng", Toast.LENGTH_SHORT).show();
        });
    }

    //interface
    public interface OnItemClickListener {
        void onItemClick(Pet pet);
    }

    public interface OnEditClickListener {
        void onEditClick(Pet pet);
    }

    private OnItemClickListener itemClickListener;
    private OnEditClickListener editClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }



}

