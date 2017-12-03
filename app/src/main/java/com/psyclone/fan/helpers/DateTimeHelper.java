package com.psyclone.fan.helpers;

import java.util.Calendar;

public class DateTimeHelper {
    public static String getTodaysDateString() {
        Calendar today = Calendar.getInstance();
        return String.format("%02d%02d%d", today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH)+1, today.get(Calendar.YEAR));
    }

    public static String getTomorrowsDateString() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);

        return String.format("%02d%02d%d", tomorrow.get(Calendar.DAY_OF_MONTH), tomorrow.get(Calendar.MONTH)+1, tomorrow.get(Calendar.YEAR));
    }

    public static String reverseDate(String date) {
        return (date.substring(4, 8) + date.substring(2, 4) + date.substring(0, 2));
    }

    public static String reverseDateWithHyphens(String date) {
        return (date.substring(4, 8) + "-" + date.substring(2, 4) + "-" + date.substring(0, 2));
    }

    public static boolean startsAfterCurrentTime(String timeString) {
        String[] pieces = timeString.split(":");
        int time = Integer.parseInt(pieces[0] + pieces[1]);

        Calendar now = Calendar.getInstance();
        int currentTime = now.get(Calendar.HOUR_OF_DAY)*100 + now.get(Calendar.MINUTE);

        return time > currentTime;
    }

    public static String covertTo12Hour(String time) {
        String[] hoursMinutes = time.split(":");
        int hours = Integer.parseInt(hoursMinutes[0]);

        if(hours == 0)
            return "12:" + hoursMinutes[1] + " AM";
        else if(hours == 12)
            return "12:" + hoursMinutes[1] + " PM";
        else if(hours > 12)
            return (hours-12) + ":" + hoursMinutes[1] + " PM";
        else
            return hours + ":" + hoursMinutes[1] + " AM";
    }
}
