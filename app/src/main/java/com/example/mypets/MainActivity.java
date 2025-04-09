package com.example.mypets;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import com.example.mypets.databinding.ActivityMainBinding;
import com.example.mypets.ui.Vaccin.VaccineReminderWorker;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private SensorManager sensorManager;
    private Sensor tempSensor;
    private Sensor humiditySensor;

    private static final float MIN_TEMP = 18.0f;
    private static final float MAX_TEMP = 30.0f;
    private static final float MIN_HUMIDITY = 40.0f;
    private static final float MAX_HUMIDITY = 65.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Yêu cầu quyền nếu Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    100
            );
        }

        createNotificationChannel();
        scheduleVaccineReminder();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_my_pet, R.id.nav_add_pet, R.id.nav_Cal_vaccine)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Firebase init
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Khởi tạo và đăng ký Sensor
        initSensors();
    }

    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

            if (tempSensor != null)
                sensorManager.registerListener(sensorListener, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);

            if (humiditySensor != null)
                sensorManager.registerListener(sensorListener, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float value = event.values[0];

            if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                if (value > MAX_TEMP) {
                    showNotification("Nhiệt độ cao: " + value + "°C. Thú cưng có thể bị sốc nhiệt.", Sensor.TYPE_AMBIENT_TEMPERATURE);
                } else if (value < MIN_TEMP) {
                    showNotification("Nhiệt độ thấp: " + value + "°C. Thú cưng có thể bị cảm lạnh.", Sensor.TYPE_AMBIENT_TEMPERATURE);
                }
            }

            if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
                if (value > MAX_HUMIDITY) {
                    showNotification("Độ ẩm cao: " + value + "%. Môi trường dễ sinh nấm mốc.", Sensor.TYPE_RELATIVE_HUMIDITY);
                } else if (value < MIN_HUMIDITY) {
                    showNotification("Độ ẩm thấp: " + value + "%. Da thú cưng có thể bị khô.", Sensor.TYPE_RELATIVE_HUMIDITY);
                }
            }
        }



        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void showNotification(String message, int sensorType) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "pet_alert_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Cảnh báo thú cưng",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        int iconRes = R.drawable.ic_vaccine; // mặc định

        if (sensorType == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            iconRes = R.drawable.ic_temp;
        } else if (sensorType == Sensor.TYPE_RELATIVE_HUMIDITY) {
            iconRes = R.drawable.ic_humi;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(iconRes)
                .setContentTitle("Cảnh báo môi trường")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "vaccine_reminders",
                    "Nhắc lịch tiêm phòng",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Thông báo nhắc lịch tiêm phòng cho thú cưng");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void scheduleVaccineReminder() {
        PeriodicWorkRequest reminderRequest = new PeriodicWorkRequest.Builder(
                VaccineReminderWorker.class,
                24,
                TimeUnit.HOURS
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "VaccineReminder",
                ExistingPeriodicWorkPolicy.REPLACE,
                reminderRequest
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorListener);
        }
    }
}
