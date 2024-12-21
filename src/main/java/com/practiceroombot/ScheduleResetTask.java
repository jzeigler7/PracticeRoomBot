package com.practiceroombot;

import com.practiceroombot.Schedule;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TimeZone;

public class ScheduleResetTask extends TimerTask {

    @Override
    public void run() {
        // Call the reset method in Schedule
        Schedule.resetCalendar();
        System.out.println("Schedule has been reset.");
    }

    public static void startResetScheduleTimer() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 19); // 7 PM
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If current time is past the target time, schedule for next week
        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleResetTask(), calendar.getTime(), 604800000L); // 604800000L milliseconds in a week
    }
}
