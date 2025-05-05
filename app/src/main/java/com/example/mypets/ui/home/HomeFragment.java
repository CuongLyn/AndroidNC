package com.example.mypets.ui.home;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mypets.R;
import com.google.firebase.Firebase;

public class HomeFragment extends Fragment implements SensorEventListener {
    private CardView alertCard;
    private TextView textTemperature, textHumidity;
    private SensorManager sensorManager;
    private Sensor tempSensor, humiditySensor;

    private static final float MIN_TEMP = 18;
    private static final float MAX_TEMP = 30;
    private static final float MIN_HUMIDITY = 40;
    private static final float MAX_HUMIDITY = 65;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ view
        alertCard = view.findViewById(R.id.alertCard);
        textTemperature = view.findViewById(R.id.textTemperature);
        textHumidity = view.findViewById(R.id.textHumidity);

        // Khởi tạo cảm biến
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        initSensors();

        setupNavigation(view);
        return view;
    }

    private void initSensors() {
        tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        // Kiểm tra cảm biến có sẵn không
        if (tempSensor == null && humiditySensor == null) {
            alertCard.setVisibility(View.GONE);
            return;
        }

        // Đăng ký listener
        if (tempSensor != null) {
            sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (humiditySensor != null) {
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isAdded()) return;

        float value = event.values[0];


        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            if (!isAdded()) return;

            if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                updateTemperatureUI(value);
            } else if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
                updateHumidityUI(value);
            }

            checkEnvironmentalStatus();
        });
    }


    private void updateTemperatureUI(float temp) {
        textTemperature.setText(String.format("Nhiệt độ: %.1f°C", temp));
    }

    private void updateHumidityUI(float humidity) {
        textHumidity.setText(String.format("Độ ẩm: %.1f%%", humidity));
    }

    private void checkEnvironmentalStatus() {
        boolean isTempSafe = true;
        boolean isHumiditySafe = true;

        // Lấy giá trị từ TextView (nếu cảm biến không khả dụng)
        try {
            String tempText = textTemperature.getText().toString();
            float temp = Float.parseFloat(tempText.replaceAll("[^\\d.]", ""));
            isTempSafe = (temp >= MIN_TEMP && temp <= MAX_TEMP);
        } catch (Exception e) {
            // Xử lý lỗi parse
        }

        try {
            String humidityText = textHumidity.getText().toString();
            float humidity = Float.parseFloat(humidityText.replaceAll("[^\\d.]", ""));
            isHumiditySafe = (humidity >= MIN_HUMIDITY && humidity <= MAX_HUMIDITY);
        } catch (Exception e) {
            // Xử lý lỗi parse
        }
        if (!isAdded()) return;

        Context context = getContext();
        if (context == null) return;

        if (isTempSafe && isHumiditySafe) {
            alertCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.safe_green));
        } else {
            alertCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.danger_red));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Không cần xử lý
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null) {
            if (tempSensor != null) sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
            if (humiditySensor != null) sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private void setupNavigation(View view) {
        int[] cardIds = {
                R.id.cardMyPet,
                R.id.cardSchedule,
                R.id.cardClinic
        };

        int[] navDirections = {
                R.id.nav_my_pet,
                R.id.nav_schedule,
                R.id.nav_clinic
        };

        for (int i = 0; i < cardIds.length; i++) {
            CardView card = view.findViewById(cardIds[i]);
            int direction = navDirections[i];

            card.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(v);

                // Tạo NavOptions để clear back stack
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_home, true)
                        .build();

                navController.navigate(direction, null, navOptions);
            });
        }
    }
}