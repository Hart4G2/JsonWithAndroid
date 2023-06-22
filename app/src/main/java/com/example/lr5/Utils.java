package com.example.lr5;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static boolean isTimeValid(String time) {
        if (time.length() != 5) {
            return false;
        }

        String[] parts = time.split(":");
        if (parts.length != 2 || parts[0].length() != 2 || parts[1].length() != 2) {
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateFormat.setLenient(false);

        try {
            Date parsedDate = dateFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static long convertTimeToMillis(String time) {
        String[] timeParts = time.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
}
