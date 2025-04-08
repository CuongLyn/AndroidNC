package com.example.mypets.ui.Vaccin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.Manifest;
import com.example.mypets.R;
import com.example.mypets.data.model.Vaccination;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.Result;

public class VaccineReminderWorker extends Worker {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public VaccineReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        checkVaccineDates();
        return Result.success();
    }

    private void checkVaccineDates() {
        FirebaseDatabase.getInstance().getReference("pets")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                            String petName = petSnapshot.child("name").getValue(String.class);
                            DataSnapshot vaccines = petSnapshot.child("vaccinations");

                            for (DataSnapshot vaccine : vaccines.getChildren()) {
                                checkAndNotify(petName, vaccine.getValue(Vaccination.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("VaccineReminder", "Lỗi đọc dữ liệu: " + error.getMessage());
                    }
                });
    }

    private void checkAndNotify(String petName, Vaccination vaccination) {
        try {
            Date currentDate = new Date();

            // Kiểm tra ngày tiêm
            if (vaccination.getDate() != null) {
                Date vaccineDate = dateFormat.parse(vaccination.getDate());
                checkDateDifference(petName, vaccineDate, currentDate, "tiêm");
            }

            // Kiểm tra ngày tiêm tiếp theo
            if (vaccination.getNextDate() != null) {
                Date nextDate = dateFormat.parse(vaccination.getNextDate());
                checkDateDifference(petName, nextDate, currentDate, "tiêm tiếp theo");
            }
        } catch (ParseException e) {
            Log.e("DateParse", "Lỗi định dạng ngày: " + e.getMessage());
        }
    }

    private void checkDateDifference(String petName, Date targetDate, Date currentDate, String type) {
        long diff = targetDate.getTime() - currentDate.getTime();
        int daysLeft = (int) (diff / (1000 * 60 * 60 * 24));

        if (daysLeft >= 0 && daysLeft <= 3) { // Thông báo trước 3 ngày
            sendNotification(
                    "Nhắc lịch tiêm phòng",
                    "Còn " + daysLeft + " ngày đến lịch " + type + " cho " + petName
            );
        }
    }

    private void sendNotification(String title, String message) {
            String channelId = "vaccine_reminders";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(R.drawable.ic_vaccine)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

            if (ActivityCompat.checkSelfPermission(
                    getApplicationContext(),
                    Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED) {
                manager.notify((int) System.currentTimeMillis(), builder.build());
            }
    }
}