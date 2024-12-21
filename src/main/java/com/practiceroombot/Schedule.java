package com.practiceroombot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Represents the schedule for room reservations and raids in the practice room bot.
 * This class manages and tracks reservations and raids for different rooms and times.
 */
public class Schedule {

    public static final int SLOTS_PER_DAY = 48;
    private static final int DAYS_PER_WEEK = 7; // Change to 7 since we cover a full week
    public static final int SLOTS_PER_WEEK = DAYS_PER_WEEK * SLOTS_PER_DAY; // 336
    // Logger for logging errors and information
    private static final Logger logger = LoggerFactory.getLogger(Schedule.class);

    // Constants defining schedule dimensions and specific times
    // Adjust the constants for the schedule dimensions


    private static final int MONDAY_EVENING_START_SLOT = 39; // 7:30 PM in half-hour slots

    // Arrays representing the state of the schedule for reservations and raids
    static boolean[] raids = new boolean[SLOTS_PER_WEEK];
    static String[] pr1schedule = new String[SLOTS_PER_WEEK];
    static String[] pr2schedule = new String[SLOTS_PER_WEEK];

    /**
     * Constructs a new Schedule object and initializes the schedule to a clean state.
     */
    public Schedule() {
        resetCalendar();
    }

    /**
     * Resets the calendar, clearing all reservations and raids by setting the
     * respective arrays to their initial values (false for raids, null for reservations).
     */
    public static void resetCalendar() {
        Arrays.fill(raids, false);
        Arrays.fill(pr1schedule, null);
        Arrays.fill(pr2schedule, null);
    }

    /**
     * Attempts to add a reservation for a room.
     * Validates the room number, time, and duration, then checks for overlapping reservations
     * or total usage limits before reserving the requested time slots.
     *
     * @param roomNumber The number of the room to reserve.
     * @param user The name of the user making the reservation.
     * @param startTime The starting slot index for the reservation.
     * @param duration The duration of the reservation in half-hour increments.
     * @return true if the reservation was successful, false otherwise.
     */
    public static String addReservation(int roomNumber, String user, int startTime, double duration) {
        validateRoomNumber(roomNumber);

        int endTime = calculateEndTime(startTime, duration);
        if (!isValidTimeIndex(startTime) || !isValidTimeIndex(endTime)) {
            return "Failed: Cannot span across Monday evening split.";
        }

        try {
            checkUserLimit(user, duration);
        } catch (IllegalArgumentException e) {
            return "Failed: You can only reserve 3 hours of practice time per week!";
        }

        if (hasOverlappingReservation(user, startTime, endTime, roomNumber)) {
            return "Failed: You cannot reserve both rooms at the same time!";
        }

        if (!areSlotsAvailable(roomNumber, startTime, endTime)) {
            return "Failed: This room is already reserved at this time.";
        }

        if (doesCrossMondaySplit(startTime, duration)) {
            return "Failed: Cannot span across Monday evening split.";
        }

        reserveSlots(roomNumber, user, startTime, endTime);
        return "Congrats! You've reserved room " + roomNumber + ". Happy practicing!";
    }


    /**
     * Checks if the slots required for the reservation are available.
     *
     * @param roomNumber The number of the room for the reservation.
     * @param startTime The start time of the reservation.
     * @param endTime   The end time of the reservation.
     * @return true if all slots are available, false otherwise.
     */
    static boolean areSlotsAvailable(int roomNumber, int startTime, int endTime) {
        String[] schedule = roomNumber == 1 ? pr1schedule : pr2schedule;
        for (int i = startTime; i != endTime; i++) {
            if (schedule[i] != null) {
                return false;
            }
        }
        return true;
    }


    /**
     * Attempts to add a raid marking to the schedule.
     * Validates the time and duration, then marks the specified slots as a raid.
     *
     * @param startTime The starting slot index for the raid.
     * @param duration The duration of the raid in half-hour increments.
     * @return A message indicating the result of the operation.
     */
    public static String addRaid(int startTime, double duration) {
        startTime = adjustStartTimeForMonday(startTime);
        if (!isValidTimeIndex(startTime) || !isValidTimeIndex(calculateEndTime(startTime, duration))) {
            return "Failed: Invalid start time or duration.";
        }

        try {
            checkRaidValidity(startTime, duration);
        } catch (IllegalArgumentException e) {
            return "Failed: " + e.getMessage();
        }

        int endTime = calculateEndTime(startTime, duration);
        reserveRaidSlots(startTime, endTime);
        return "Raid scheduled successfully from " + startTime + " to " + endTime;
    }


    /**
     * Attempts to remove a raid marking from the schedule within a range.
     * Identifies the start and end of a raid around a given time index and clears the marking.
     *
     * @param timeIndex The index of the time slot within the raid range to be cleared.
     * @return true if a raid marking was removed, false otherwise.
     */
    public static boolean removeRaidInRange(int timeIndex) {
        // Method implementation to remove raid marking...
        if (!isValidTimeIndex(timeIndex)) {
            logger.error("Failed: Invalid time index provided: {}", timeIndex);
            return false;
        }

        int start = findRaidStart(timeIndex);
        int end = findRaidEnd(timeIndex);
        Arrays.fill(raids, start, end, false);
        return true;
    }

    /**
     * Validates the provided room number is either 1 or 2.
     *
     * @param roomNumber The room number to validate.
     * @throws IllegalArgumentException if the room number is neither 1 nor 2.
     */
    private static void validateRoomNumber(int roomNumber) {
        if (roomNumber != 1 && roomNumber != 2) {
            logger.error("Invalid room number: {}", roomNumber);
            throw new IllegalArgumentException("Invalid room number. Must be 1 or 2.");
        }
    }

    /**
     * Attempts to add or extend a recording session in the schedule.
     * Recording sessions occupy both practice rooms and can override existing recordings.
     *
     * @param startTime The starting slot index for the recording session.
     * @param duration  The duration of the recording session in half-hour increments.
     * @return A message indicating the result of the operation.
     */
    public static String addRecordingSession(int startTime, double duration) {
        int endTime = calculateEndTime(startTime, duration);

        if (!isValidTimeIndex(startTime) || !isValidTimeIndex(endTime)) {
            return "Failed: Invalid start or end time for recording session.";
        }

        // This check allows the session to extend existing recording sessions
        if (!areSlotsAvailableOrExtendable(startTime, endTime)) {
            return "Failed: Slots are not available or extendable for recording session.";
        }

        reserveRecordingSlots(startTime, endTime);
        return "Recording session scheduled successfully from index " + startTime + " to " + endTime;
    }


    /**
     * Cancels a recording session from the schedule based on a given index within the session.
     *
     * @param timeIndex The index of a time slot within the recording session to be cancelled.
     */
    public static void cancelRecordingSession(int timeIndex) {
        // Validate the provided index
        if (!isValidTimeIndex(timeIndex)) {
            logger.error("Invalid time index for cancelling recording session.");
            return;
        }

        // Check if the index corresponds to a recording session
        if (!isRecordingSession(timeIndex)) {
            logger.error("The provided index does not correspond to a recording session.");
            return;
        }

        // Find the start and end indices of the recording session
        int start = findRecordingSessionStart(timeIndex);
        int end = findRecordingSessionEnd(timeIndex);

        // Cancel slots occupied by the recording session
        cancelRecordingSlots(start, end);
    }

    /**
     * Checks if the given index in the schedule corresponds to a recording session.
     *
     * @param timeIndex The index to check in the schedule.
     * @return true if the index corresponds to a recording session, false otherwise.
     */
    static boolean isRecordingSession(int timeIndex) {
        return "Recording Session".equals(pr1schedule[timeIndex]) || "Recording Session".equals(pr2schedule[timeIndex]);
    }

    /**
     * Finds the start index of a recording session in the schedule arrays.
     *
     * @param timeIndex The index of a time slot within the recording session.
     * @return The start index of the recording session.
     */
    private static int findRecordingSessionStart(int timeIndex) {
        int startIndex = timeIndex;
        while (startIndex > 0 && "Recording Session".equals(pr1schedule[startIndex - 1])) {
            startIndex--; // Moving backwards to find the start
        }
        return startIndex;
    }

    /**
     * Finds the end index of a recording session in the schedule arrays.
     *
     * @param timeIndex The index of a time slot within the recording session.
     * @return The end index of the recording session.
     */
    private static int findRecordingSessionEnd(int timeIndex) {
        int endIndex = timeIndex;
        while (endIndex < SLOTS_PER_WEEK && "Recording Session".equals(pr1schedule[endIndex])) {
            endIndex++; // Moving forward to find the end
        }
        return endIndex;
    }

    /**
     * Checks if slots are either available or already occupied by a recording session,
     * allowing for extension of existing recording sessions.
     *
     * @param startTime The start time of the recording session.
     * @param endTime   The end time of the recording session.
     * @return true if all slots are available or can be extended, false otherwise.
     */
    private static boolean areSlotsAvailableOrExtendable(int startTime, int endTime) {
        for (int i = startTime; i != endTime; i = (i + 1) % SLOTS_PER_WEEK) {
            if (!(pr1schedule[i] == null || "Recording Session".equals(pr1schedule[i])) &&
                    !(pr2schedule[i] == null || "Recording Session".equals(pr2schedule[i]))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cancels slots occupied by a recording session in both practice rooms.
     *
     * @param startTime The start time of the recording session.
     * @param endTime   The end time of the recording session.
     */
    private static void cancelRecordingSlots(int startTime, int endTime) {
        for (int i = startTime; i < endTime; i++) {
            if (i < SLOTS_PER_WEEK) {
                if ("Recording Session".equals(pr1schedule[i])) {
                    pr1schedule[i] = null; // Clearing in Room 1
                }
                if ("Recording Session".equals(pr2schedule[i])) {
                    pr2schedule[i] = null; // Clearing in Room 2
                }
            }
        }
    }


    /**
     * Reserves slots for a recording session in both practice rooms.
     *
     * @param startTime The start time of the recording session.
     * @param endTime   The end time of the recording session.
     */
    private static void reserveRecordingSlots(int startTime, int endTime) {
        for (int i = startTime; i < endTime; i++) {
            if (i < SLOTS_PER_WEEK) {
                pr1schedule[i] = "Recording Session"; // Reserving in Room 1
                pr2schedule[i] = "Recording Session"; // Reserving in Room 2
            }
        }
    }

    /**
     * Adjusts the start time for reservations to ensure they do not start on Monday evening.
     *
     * @param startTime The original start time of the reservation.
     * @return The adjusted start time.
     */
    private static int adjustStartTimeForMonday(int startTime) {
        int day = startTime / SLOTS_PER_DAY;
        int hour = startTime % SLOTS_PER_DAY;
        // Adjust start time to next week if it is First Monday before 7:30 PM
        if (day == 0 && hour < MONDAY_EVENING_START_SLOT) {
            return startTime + (SLOTS_PER_DAY * DAYS_PER_WEEK);
        }
        // Adjust start time to beginning of the week if it is Last Monday after 7:30 PM
        if (day == 7 && hour >= MONDAY_EVENING_START_SLOT) {
            return startTime - (SLOTS_PER_DAY * (DAYS_PER_WEEK - 1));
        }
        return startTime;
    }

    /**
     * Checks the validity of a raid by ensuring it does not cross the Monday evening split.
     *
     * @param startTime The start time of the raid.
     * @param duration  The duration of the raid.
     * @throws IllegalArgumentException if the raid crosses the Monday split.
     */
    private static void checkRaidValidity(int startTime, double duration) {
        if (doesCrossMondaySplit(startTime, duration)) {
            logger.error("Raid crosses the Monday 7:30 PM split. Start time: {}, Duration: {}", startTime, duration);
            throw new IllegalArgumentException("Raid cannot cross the Monday 7:30 PM split.");
        }
    }

    /**
     * Checks if the user's total reservations, including the current one, exceed the allowed limit.
     *
     * @param user     The user for whom to count the reservations.
     * @param duration The duration of the current reservation.
     * @throws IllegalArgumentException if the total duration exceeds the allowed limit.
     */
    static void checkUserLimit(String user, double duration) {
        long totalOccurrences = countUserOccurrences(user);
        if (duration + (totalOccurrences / 2.0) > 3.0) {
            logger.error("Duration and total occurrences exceed the limit for user: {}", user);
            throw new IllegalArgumentException("Duration and total occurrences exceed the limit.");
        }
    }

    /**
     * Calculates the end time of a reservation or raid based on its start time and duration.
     *
     * @param startTime The start time of the reservation or raid.
     * @param duration  The duration of the reservation or raid.
     * @return The end time as an integer index.
     */
    static int calculateEndTime(int startTime, double duration) {
        return startTime + (int) (duration * 2);
    }

    /**
     * Reserves the specified slots for a given user in a given room.
     *
     * @param roomNumber The number of the room for the reservation.
     * @param user       The name of the user making the reservation.
     * @param startTime  The start time of the reservation.
     * @param endTime    The end time of the reservation.
     */
    static void reserveSlots(int roomNumber, String user, int startTime, int endTime) {
        String[] schedule = roomNumber == 1 ? pr1schedule : pr2schedule;
        for (int i = startTime; i < endTime; i++) {
            if (i < SLOTS_PER_WEEK) {
                schedule[i] = user;
            }
        }
    }

    /**
     * Reserves slots for a raid within the given time range.
     *
     * @param startTime The start time of the raid.
     * @param endTime   The end time of the raid.
     */
    private static void reserveRaidSlots(int startTime, int endTime) {
        Arrays.fill(raids, startTime, Math.min(endTime, SLOTS_PER_WEEK), true);
    }

    /**
     * Checks if a reservation or raid crosses the Monday evening split.
     *
     * @param startTime The start time of the reservation or raid.
     * @param duration  The duration of the reservation or raid.
     * @return true if it crosses the Monday split, false otherwise.
     */
    private static boolean doesCrossMondaySplit(int startTime, double duration) {
        int endTime = calculateEndTime(startTime, duration);
        // Check if the reservation crosses over to the next week
        return (endTime >= SLOTS_PER_WEEK + 1);
    }

    /**
     * Counts the total number of occurrences of a user's reservations in both rooms.
     *
     * @param user The name of the user to check for.
     * @return The total number of occurrences of the user's reservations.
     */
    private static long countUserOccurrences(String user) {
        return countOccurrences(pr1schedule, user) + countOccurrences(pr2schedule, user);
    }

    /**
     * Counts the occurrences of a user's reservations in a given schedule array.
     *
     * @param schedule The schedule array to check in.
     * @param user     The name of the user to check for.
     * @return The number of occurrences of the user's reservations.
     */
    private static long countOccurrences(String[] schedule, String user) {
        return Arrays.stream(schedule).filter(user::equals).count();
    }

    /**
     * Cancels a user's reservation starting at a specified time in a given schedule array.
     *
     * @param isRoomOne If true, interface with room one. If false, interface with room two.
     * @param user      The name of the user whose reservation is to be canceled.
     * @param timeIndex The index of the time slot at which the reservation starts.
     */
    static void cancelReservationInRoom(boolean isRoomOne, String user, int timeIndex) {
        String[] selectedSchedule = isRoomOne ? pr1schedule : pr2schedule;

        if (isValidTimeIndex(timeIndex) && user.equals(selectedSchedule[timeIndex])) {
            int start = findReservationStart(selectedSchedule, user, timeIndex);
            int end = findReservationEnd(selectedSchedule, user, timeIndex);

            for (int i = start; i < end; i++) {
                if (i < SLOTS_PER_WEEK) {
                    selectedSchedule[i] = null;
                }
            }
        }
    }

    /**
     * Finds the start index of a user's reservation in a given schedule array.
     *
     * @param schedule  The schedule array to check in.
     * @param user      The name of the user whose reservation start is to be found.
     * @param timeIndex The index of a time slot within the reservation.
     * @return The start index of the reservation.
     */
    static int findReservationStart(String[] schedule, String user, int timeIndex) {
        int startIndex = timeIndex;
        while (startIndex > 0 && user.equals(schedule[startIndex - 1])) {
            startIndex--;
        }
        return startIndex;
    }

    /**
     * Finds the end index of a user's reservation in a given schedule array.
     *
     * @param schedule  The schedule array to check in.
     * @param user      The name of the user whose reservation end is to be found.
     * @param timeIndex The index of a time slot within the reservation.
     * @return The end index of the reservation.
     */
    static int findReservationEnd(String[] schedule, String user, int timeIndex) {
        int endIndex = timeIndex;
        while (endIndex < SLOTS_PER_WEEK && user.equals(schedule[endIndex])) {
            endIndex++;
        }
        return endIndex;
    }

    /**
     * Checks if there is an overlapping reservation for a user in a different room.
     *
     * @param user        The user to check for overlapping reservations.
     * @param startTime   The start time of the new reservation.
     * @param endTime     The end time of the new reservation.
     * @param excludedRoomNumber The room number where the reservation is not being checked.
     * @return true if there is an overlapping reservation, false otherwise.
     */
    static boolean hasOverlappingReservation(String user, int startTime, int endTime, int excludedRoomNumber) {
        String[] otherRoomSchedule = excludedRoomNumber == 1 ? pr2schedule : pr1schedule;
        for (int i = startTime; i < endTime; i++) {
            if (i >= SLOTS_PER_WEEK) {
                continue; // Skip invalid indices
            }
            if (user.equals(otherRoomSchedule[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a given slot index is within the valid range of the schedule.
     *
     * @param index The index to check.
     * @return true if the index is valid, false otherwise.
     */
    static boolean isValidTimeIndex(int index) {
        return index >= 0 && index < (SLOTS_PER_WEEK + 2);
    }

    /**
     * Finds the start index of a raid in the schedule array.
     *
     * @param timeIndex The index of a time slot within the raid.
     * @return The start index of the raid.
     */
    private static int findRaidStart(int timeIndex) {
        int startIndex = timeIndex;
        while (startIndex > 0 && raids[startIndex - 1]) {
            startIndex--;
        }
        return startIndex;
    }

    /**
     * Finds the end index of a raid in the schedule array.
     *
     * @param timeIndex The index of a time slot within the raid.
     * @return The end index of the raid.
     */
    private static int findRaidEnd(int timeIndex) {
        int endIndex = timeIndex;
        while (endIndex < SLOTS_PER_WEEK && raids[endIndex]) {
            endIndex++;
        }
        return endIndex;
    }

    /**
     * Checks if a user has a reservation in a specific room at a specific time slot.
     *
     * @param user      The name of the user.
     * @param isRoomOne True if checking in Room 1, false if in Room 2.
     * @param timeIndex The index of the time slot.
     * @return true if the user has a reservation at the specified time slot in the specified room, false otherwise.
     */
    public static boolean isUserReservationPresent(String user, boolean isRoomOne, int timeIndex) {
        if (!isValidTimeIndex(timeIndex)) {
            logger.error("Invalid time index: {}", timeIndex);
            return false;
        }

        String[] schedule = isRoomOne ? pr1schedule : pr2schedule;
        return user.equals(schedule[timeIndex]);
    }

    public static String getReservation(int roomNumber, int timeIndex) {
        String[] schedule = roomNumber == 1 ? pr1schedule : pr2schedule;
        if (isValidTimeIndex(timeIndex)) {
            return schedule[timeIndex];
        } else {
            throw new IllegalArgumentException("Invalid time index: " + timeIndex);
        }
    }
}
