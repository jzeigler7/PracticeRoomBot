package com.practiceroombot;


public class TimeIntegerizer {

    private static final int MONDAY_EVENING_START_INDEX = 39; // Index for Monday 7:30 PM
    private static final int SLOTS_PER_DAY = 48; // Number of time slots per day
    private static final int SLOTS_PER_WEEK = 336; // Number of time slots per day

    /**
     * Converts a given time and day into an index representing its position in the schedule.
     *
     * @param preIntegerizedTime The time in a human-readable format (e.g., "8:30 pm").
     * @param day The day of the week.
     * @return The index corresponding to the provided time and day in the schedule.
     * @throws IllegalArgumentException If the day or time format is invalid.
     */
    public static int integerizeTime(String preIntegerizedTime, String day) throws IllegalArgumentException {
        String normalizedTime = normalizeTime(preIntegerizedTime);
        String normalizedDay = normalizeDay(day);

        int dayIndex = convertDayToIndex(normalizedDay);
        int timeIndex = convertTimeToIndex(normalizedTime);

        // Adjust for special cases involving Monday
        return calculateFinalIndex(dayIndex, timeIndex) - 39;
    }

    /**
     * Normalizes the time input to a consistent format.
     * This method now handles 'A' and 'P' as valid time suffixes in addition to 'AM' and 'PM'.
     *
     * @param time The time string to normalize.
     * @return A normalized time string.
     */
    private static String normalizeTime(String time) {
        // Handle cases where 'A' or 'P' is used instead of 'AM' or 'PM'
        time = time.toLowerCase().replaceAll("[^0-9:apm]", "");

        if (time.endsWith("a") || time.endsWith("p")) {
            time += "m";
        }

        // Handle cases where the time is provided without minutes
        if (time.matches("^(1[0-2]|0?[1-9])[ap]m$")) {
            time = time.substring(0, time.length() - 2) + ":00" + time.substring(time.length() - 2);
        }

        return time;
    }

    /**
     * Normalizes the day input to a consistent format.
     *
     * @param day The day string to normalize.
     * @return A normalized day string.
     */
    private static String normalizeDay(String day) {
        return day.replaceAll("[^a-zA-Z]", "").toLowerCase();
    }

    /**
     * Converts a day string to its corresponding index in the week.
     *
     * @param day The normalized day string.
     * @return The index of the day in the week.
     * @throws IllegalArgumentException If the provided day is invalid.
     */
    static int convertDayToIndex(String day) throws IllegalArgumentException {
        return switch (day) {
            case "monday" -> 0;
            case "tuesday" -> 1;
            case "wednesday" -> 2;
            case "thursday" -> 3;
            case "friday" -> 4;
            case "saturday" -> 5;
            case "sunday" -> 6;
            default ->
                    throw new IllegalArgumentException("Invalid day provided: '" + day + "'. Please provide a valid weekday.");
        };
    }

    /**
     * Converts a time string to its corresponding index in the day.
     * Validates that the time is on-the-half-hour and rejects otherwise.
     * Automatically determines if the time is in 12-hour or 24-hour format.
     *
     * @param time The normalized time string.
     * @return The index representing the specified time in the schedule.
     * @throws IllegalArgumentException If the time format is invalid or off-the-half-hour.
     */
    public static int convertTimeToIndex(String time) throws IllegalArgumentException {
        time = normalizeTime(time);
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
        int minutes = parts.length > 1 ? Integer.parseInt(parts[1].substring(0, 2)) : 0;
        boolean isPM = parts.length > 1 && parts[1].toLowerCase().contains("p");

        if (minutes % 30 != 0) { // Checks if the minutes are off-the-half-hour
            throw new IllegalArgumentException("Time must be on-the-half-hour.");
        }

        // Determine if the time format is 12-hour based on the presence of AM/PM
        boolean isTwelveHourFormat = time.matches(".*[ap]m.*");

        // Adjust the hour for 24-hour format based on AM/PM notation, if needed
        int hourIn24Format = isTwelveHourFormat ? adjustHourFor24HourFormat(hour, isPM) : hour;

        return calculateTimeIndex(hourIn24Format, minutes);
    }

    /**
     * Checks whether the given string is a valid time format.
     * Valid formats include "8:00 am", "8 am", "8:00", "8a", "8p",
     * and the time should be on-the-half-hour.
     *
     * @param time The string to check.
     * @return True if the string is a valid time format, false otherwise.
     */
    public static boolean isValidTimeFormat(String time) {
        try {
            String normalizedTime = normalizeTime(time);

            // Check if the time matches the expected pattern
            if (!normalizedTime.matches("(\\d{1,2})(:\\d{2})?\\s?(am|pm|a|p)?")) {
                return false;
            }

            // Extract hour and minutes and check for "on-the-half-hour" times
            String[] parts = normalizedTime.split(":");
            int minutes = parts.length > 1 ? Integer.parseInt(parts[1].substring(0, 2)) : 0;

            return minutes % 30 == 0;
        } catch (Exception e) {
            // If any parsing error occurs, return false
            return false;
        }
    }


    /**
     * Adjusts the hour for a 24-hour format based on AM/PM notation.
     *
     * @param hour The hour to adjust.
     * @param isPM True if the time is in PM, false if in AM.
     * @return The adjusted hour in 24-hour format.
     */
    private static int adjustHourFor24HourFormat(int hour, boolean isPM) {
        if (isPM && hour != 12) hour += 12;
        else if (!isPM && hour == 12) hour = 0;
        return hour;
    }

    /**
     * Calculates the time index based on hour and minutes.
     *
     * @param hour    The hour part of the time.
     * @param minutes The minutes part of the time.
     * @return The index representing the specified time in the schedule.
     */
    private static int calculateTimeIndex(int hour, int minutes) {
        // Handle midnight case
        if (hour == 24) {
            hour = 0; // Reset to 0 for a new day
        }
        return (hour * 2) + (minutes >= 30 ? 1 : 0);
    }

    /**
     * Calculates the final index in the schedule based on day and time indices.
     *
     * @param dayIndex   The index of the day.
     * @param timeIndex  The index of the time.
     * @return The final index in the schedule.
     */
    private static int calculateFinalIndex(int dayIndex, int timeIndex) {
        if (dayIndex == 0 && timeIndex < MONDAY_EVENING_START_INDEX) {
            // For First Monday before 7:30 PM, adjust index to reflect it as the end of the previous week
            return SLOTS_PER_WEEK + timeIndex;
        } else if (dayIndex == 7 && timeIndex >= MONDAY_EVENING_START_INDEX) {
            // For Last Monday after 7:30 PM, adjust index to reflect it as the start of the week
            return timeIndex - MONDAY_EVENING_START_INDEX;
        } else {
            // For other days and times, calculate the index normally
            return (dayIndex * SLOTS_PER_DAY) + timeIndex;
        }
    }
}
