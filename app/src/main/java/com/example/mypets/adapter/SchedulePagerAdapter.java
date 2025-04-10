package com.example.mypets.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mypets.ui.Schedule.DailyScheduleFragment;
import com.example.mypets.ui.Schedule.WeeklyScheduleFragment;

public class SchedulePagerAdapter extends FragmentStateAdapter {
    private static final int TAB_COUNT = 2;

    public SchedulePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new DailyScheduleFragment() : new WeeklyScheduleFragment();
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
