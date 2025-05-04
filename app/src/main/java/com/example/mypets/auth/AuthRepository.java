package com.example.mypets.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mypets.data.model.User;
import com.example.mypets.utils.Resource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AuthRepository {
    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference userRef;

    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("user");
    }

    public LiveData<Resource<User>> loginUser(String email, String password) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();
                        fetchUserFromDB(uid, result);
                    } else {
                        result.postValue(Resource.error("Đăng nhập thất bại", null));
                    }
                });

        return result;
    }

    private void fetchUserFromDB(String uid, MutableLiveData<Resource<User>> result) {
        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    user.setUid(uid);
                    result.postValue(Resource.success(user));
                } else {
                    result.postValue(Resource.error("Không tìm thấy thông tin người dùng", null));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.postValue(Resource.error(error.getMessage(), null));
            }
        });
    }
}