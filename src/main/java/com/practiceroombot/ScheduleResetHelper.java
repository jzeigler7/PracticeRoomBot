package com.practiceroombot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.Duration;

/**
 * A helper class to manage periodic resetting of the schedule.
 * It uses a ScheduledExecutorService to perform resets at a specified time every week.
 */
public class ScheduleResetHelper {

    // Calculate initial delay for the first run of the reset. Set to 7:30 PM on Monday.
    private static final int INITIAL_DELAY = calculateInitialDelay(LocalTime.of(19, 30));
    private static final long PERIOD = TimeUnit.DAYS.toMillis(7); // Repeat every 7 days

    /**
     * Starts a weekly reset task to reset the schedule.
     * The reset task is scheduled to run periodically, using a ScheduledExecutorService.
     *
     */
    public static void startWeeklyReset() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Schedule the reset task to run periodically
        scheduler.scheduleAtFixedRate(Schedule::resetCalendar, INITIAL_DELAY, PERIOD, TimeUnit.MILLISECONDS);
    }

    /**
     * Calculates the initial delay to the next occurrence of the specified day and time.
     * This method computes the time difference between the current moment and the next
     * scheduled day and time, returning this duration in milliseconds.
     *
     * @param time The time of the day for the task to run.
     * @return The initial delay in milliseconds to the next occurrence of the day and time.
     */
    private static int calculateInitialDelay(LocalTime time) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime nextRun = now.with(DayOfWeek.MONDAY).with(time);

        // Adjust to the next occurrence if the time has already passed for this week
        if (now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusWeeks(1);
        }

        Duration duration = Duration.between(now, nextRun);
        return (int) duration.toMillis();
    }

    /**
     * The main method to start the schedule reset process.
     * This method serves as the entry point for starting the schedule reset helper,
     * initializing and starting the weekly reset process for the schedule.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        startWeeklyReset();
    }
}
