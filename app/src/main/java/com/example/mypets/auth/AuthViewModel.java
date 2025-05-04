package com.example.mypets.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mypets.data.model.User;
import com.example.mypets.utils.Resource;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Resource<User>> loginResult = new MutableLiveData<>();

    public AuthViewModel() {
        authRepository = new AuthRepository();
    }

    public void login(String email, String password) {
        loginResult.setValue(Resource.loading());
        authRepository.loginUser(email, password)
                .observeForever(userResource -> loginResult.postValue(userResource));
    }

    public LiveData<Resource<User>> getLoginResult() {
        return loginResult;
    }
}