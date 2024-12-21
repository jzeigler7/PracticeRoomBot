package com.practiceroombot;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class RealTimeTracker {

    /**
     * Uses TimeIntegerizer to calculate the current time index based on Eastern Standard Time (EST)
     * and floors the time to the nearest half-hour.
     * This index corresponds to the slots in the schedule managed by the PracticeRoomBot.
     *
     * @return the index in the weekly schedule that corresponds to the current real-world time,
     * floored to the nearest half-hour.
     */
    public static int getCurrentTimeIndex() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/New_York"));

        // Format day and time to match expected input for TimeIntegerizer
        String formattedDay = now.format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH)).toLowerCase();
        String formattedTime = now.withMinute(now.getMinute() < 30 ? 0 : 30).format(DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH)).toLowerCase();

        // Integerize the current day and time
        return TimeIntegerizer.integerizeTime(formattedTime, formattedDay);
    }
}


