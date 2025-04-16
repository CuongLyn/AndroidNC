package com.example.mypets.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;

import com.example.mypets.R;
import com.example.mypets.data.model.Schedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ScheduleWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScheduleRemoteViewsFactory(getApplicationContext(), intent);
    }
}

class ScheduleRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private static final String TAG = "WidgetFactory";

    private final int appWidgetId;
    private List<Schedule> schedules = new ArrayList<>();

    public ScheduleRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {
        Log.d(TAG, "onDataSetChanged for widget ID: " + appWidgetId);
        final CountDownLatch latch = new CountDownLatch(1);
        loadScheduleData(() -> {
            Log.d(TAG, "Data loading completed for widget ID: " + appWidgetId);
            latch.countDown();
        });
        try {
            latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "Data loading interrupted for widget ID: " + appWidgetId, e);
            Thread.currentThread().interrupt();
        }
    }

    private void loadScheduleData(Runnable completion) {
        String userId = "a67a506e0b17";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startOfDay = calendar.getTimeInMillis();
        long endOfDay = startOfDay + 86_400_000; // 24 giờ

        Log.d(TAG, String.format("Loading data for user: %s, start: %d, end: %d",
                userId, startOfDay, endOfDay));

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("userSchedules")
                .child(userId);

        // Thay thế addListenerForSingleValueEvent bằng addValueEventListener
        ref.orderByChild("time")
                .startAt(startOfDay)
                .endAt(endOfDay)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "Firebase data received, snapshot count: " + snapshot.getChildrenCount());
                        schedules.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Schedule schedule = data.getValue(Schedule.class);
                            if (schedule != null) {
                                schedule.setScheduleId(data.getKey());
                                schedules.add(schedule);
                            }
                        }
                        Log.d(TAG, "Schedules after processing: " + schedules.size());
                        Collections.sort(schedules, (s1, s2) -> Long.compare(s1.getTime(), s2.getTime()));
                        completion.run();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Firebase query cancelled: " + error.getMessage(), error.toException());
                        completion.run();
                    }
                });
    }
    @Override
    public RemoteViews getViewAt(int position) {
        Log.d(TAG, "Creating view at position: " + position);

        if (position >= schedules.size()){
            Log.w(TAG, "Invalid position: " + position + ", total items: " + schedules.size());
            return null;
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.item_widget_schedule);

        Schedule schedule = schedules.get(position);

        Log.d(TAG, String.format("Item %d - Time: %d, Activity: %s, Pet: %s",
                position,
                schedule.getTime(),
                schedule.getActivity(),
                schedule.getPetName()));

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = timeFormat.format(new Date(schedule.getTime()));

        views.setTextViewText(R.id.tv_time, time);
        views.setTextViewText(R.id.tv_activity, schedule.getActivity());
        views.setTextViewText(R.id.tv_pet, schedule.getPetName());

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("schedule_id", schedule.getScheduleId());
        views.setOnClickFillInIntent(R.id.widget_item_container, fillInIntent);

        return views;
    }

    @Override public int getCount() { return schedules.size(); }
    @Override public long getItemId(int position) { return position; }
    @Override public RemoteViews getLoadingView() { return null; }
    @Override public int getViewTypeCount() { return 1; }
    @Override public boolean hasStableIds() { return true; }
    @Override public void onDestroy() { schedules.clear(); }
}