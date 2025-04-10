package com.example.mypets.widget;

import android.util.Log;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.mypets.MainActivity;
import com.example.mypets.R;

import java.util.Arrays;

public class ScheduleWidget extends AppWidgetProvider {
    private static final String TAG = "ScheduleWidget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.d(TAG, "Updating widget ID: " + appWidgetId);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.schedule_widget);

        // Set click handler for title
        Intent titleIntent = new Intent(context, MainActivity.class);
        PendingIntent titlePendingIntent = PendingIntent.getActivity(
                context,
                0,
                titleIntent,
                PendingIntent.FLAG_IMMUTABLE
        );        views.setOnClickPendingIntent(R.id.widget_title, titlePendingIntent);

        // Set up list view
        Intent serviceIntent = new Intent(context, ScheduleWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.list_view, serviceIntent);
        views.setEmptyView(R.id.list_view, R.id.empty_view);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        Log.d(TAG, "Widget views updated for ID: " + appWidgetId);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate called, widget IDs: " + Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}