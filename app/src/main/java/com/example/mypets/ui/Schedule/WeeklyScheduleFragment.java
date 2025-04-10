package com.example.mypets.ui.Schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.R;
import com.example.mypets.adapter.CalendarAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeeklyScheduleFragment extends Fragment implements CalendarAdapter.OnDateClickListener {
    private RecyclerView recyclerView;
    private CalendarAdapter adapter;
    private List<Date> dates = new ArrayList<>();
    private Calendar currentCalendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_schedule, container, false);
        recyclerView = view.findViewById(R.id.recycler_calendar);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));

        setupCalendar();
        return view;
    }

    private void setupCalendar() {
        dates.clear();
        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        for (int i = 0; i < 7; i++) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        adapter = new CalendarAdapter(dates, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDateClick(Date date) {

    }

    public void setCurrentCalendar(Calendar calendar) {
        this.currentCalendar = calendar;
        setupCalendar();
    }
}