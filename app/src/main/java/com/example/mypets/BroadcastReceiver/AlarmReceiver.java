package com.example.mypets.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.mypets.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Đến giờ ăn rồi!");

        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ambao);
        mediaPlayer.start();
    }
}
