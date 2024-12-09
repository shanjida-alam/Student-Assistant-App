package com.example.studentassistantapp.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateTimeUtils {
    public static String formatDate(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        return dateFormat.format(timestamp);
    }

    public static String formatTime(long timestamp) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormat.format(timestamp);
    }
}