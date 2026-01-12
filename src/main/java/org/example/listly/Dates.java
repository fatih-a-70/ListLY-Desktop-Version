package org.example.listly;

import java.text.DateFormat;
import java.util.Date;

public class Dates {

    public static String formatDateTime(long ms) {
        return DateFormat.getDateTimeInstance().format(new Date(ms));
    }

    public static String formatDuration(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;
        if (hours > 0) return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
