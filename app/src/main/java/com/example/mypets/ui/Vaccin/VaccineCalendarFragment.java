package com.example.mypets.ui.Vaccin;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mypets.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class VaccineCalendarFragment extends Fragment {
    private MaterialCalendarView calendarView;
    private DatabaseReference petsRef;
    private final List<CalendarDay> vaccineDates = new ArrayList<>();
    private final List<CalendarDay> nextVaccineDates = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vaccine_calendar, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AndroidThreeTen.init(requireContext()); // Khởi tạo ThreeTenABP
        loadVaccineData();
    }

    private void loadVaccineData() {
        petsRef = FirebaseDatabase.getInstance().getReference("pets");
        petsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                    DataSnapshot vaccinations = petSnapshot.child("vaccinations");
                    for (DataSnapshot vaccineSnapshot : vaccinations.getChildren()) {
                        String dere = vaccineSnapshot.child("dere").getValue(String.class);
                        String nextDate = vaccineSnapshot.child("nextDate").getValue(String.class);

                        if (dere != null) addDateToCalendar(dere, true);
                        if (nextDate != null) addDateToCalendar(nextDate, false);
                    }
                }
                updateCalendarDecorators();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDateToCalendar(String dateStr, boolean isVaccineDate) {
        try {
            // Sử dụng ThreeTenABP để parse ngày
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
            CalendarDay day = CalendarDay.from(date);

            if (isVaccineDate) {
                vaccineDates.add(day);
            } else {
                nextVaccineDates.add(day);
            }
        } catch (DateTimeParseException e) {
            Log.e("DateError", "Định dạng ngày không hợp lệ: " + dateStr);
        }
    }

    private void updateCalendarDecorators() {
        // Xóa decorators cũ trước khi thêm mới
        calendarView.removeDecorators();

        calendarView.addDecorator(new EventDecorator(Color.RED, vaccineDates));
        calendarView.addDecorator(new EventDecorator(Color.BLUE, nextVaccineDates));
        calendarView.invalidateDecorators();
    }

    // Lớp Decorator
    private static class EventDecorator implements DayViewDecorator {
        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(10f, color));
        }
    }
}