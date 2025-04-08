package com.example.mypets.chart;


import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateAxisValueFormatter extends ValueFormatter {
    private final SimpleDateFormat mDateFormat;

    public DateAxisValueFormatter() {
        mDateFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
    }

    @Override
    public String getFormattedValue(float value) {
        return mDateFormat.format(new Date((long) value));
    }
}