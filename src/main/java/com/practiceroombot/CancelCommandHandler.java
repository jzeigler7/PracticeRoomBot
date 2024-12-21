package com.practiceroombot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class CancelCommandHandler implements ICommandHandler {

    @Override
    public void handleCommand(MessageReceivedEvent event, String[] parts) {
        String userMention = event.getAuthor().getAsMention(); // Get the mention string
        try {
            // Check the number of arguments
            if (parts.length != 4) {
                throw new IllegalArgumentException(" Failed: Provide three arguments: room number, day, and time.");
            }

            int roomNumber = validateRoomNumber(parts[1]);
            String day = validateDay(parts[2]);
            String time = validateTime(parts[3]);

            String user = event.getAuthor().getName();
            int timeslotIndex = TimeIntegerizer.integerizeTime(time, day);
            int currentSlotIndex = RealTimeTracker.getCurrentTimeIndex();

            boolean isRoomOne = roomNumber == 1;
            String[] schedule = isRoomOne ? Schedule.pr1schedule : Schedule.pr2schedule;

            if (Schedule.isUserReservationPresent(user, isRoomOne, timeslotIndex)) {
                int start = Schedule.findReservationStart(schedule, user, timeslotIndex);

                if (start < currentSlotIndex) {
                    event.getChannel().sendMessage(userMention + " fFailed: Cannot cancel a reservation for which part or all has already occurred.").queue();
                    return;
                }

                Schedule.cancelReservationInRoom(isRoomOne, user, start);
                event.getChannel().sendMessage(userMention + " Your reservation has been cancelled successfully.").queue();
                ScheduleImageSender.sendScheduleImage(event);
            } else {
                event.getChannel().sendMessage(userMention + " Failed: No reservation found to cancel at the specified timeslot.").queue();
            }
        } catch (IllegalArgumentException e) {
            event.getChannel().sendMessage(userMention + " Failed: " + e.getMessage()).queue();
        } catch (Exception e) {
            event.getChannel().sendMessage(userMention + " Failed: An unexpected error occurred.").queue();
            e.printStackTrace();
        }
    }


    private int validateRoomNumber(String argument) throws IllegalArgumentException {
        if (!isRoomNumber(argument)) {
            throw new IllegalArgumentException("Invalid room number: Must be either 1 or 2.");
        }
        return Integer.parseInt(argument);
    }

    private String validateDay(String argument) throws IllegalArgumentException {
        if (!isDay(argument)) {
            throw new IllegalArgumentException("Invalid day: " + argument);
        }
        return argument;
    }

    private String validateTime(String argument) throws IllegalArgumentException {
        if (!TimeIntegerizer.isValidTimeFormat(argument)) {
            throw new IllegalArgumentException("Invalid time format: " + argument);
        }
        return argument;
    }

    /**
     * Determines if the given argument is a valid room number.
     *
     * @param argument The argument to check.
     * @return True if the argument is a valid room number, false otherwise.
     */
    boolean isRoomNumber(String argument) {
        return argument.matches("1|2");
    }

    /**
     * Determines if the given argument is a valid day of the week.
     *
     * @param argument The argument to check.
     * @return True if the argument is a valid day, false otherwise.
     */
    boolean isDay(String argument) {
        String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        argument = argument.toLowerCase();
        String finalArgument = argument;
        return Arrays.stream(days).anyMatch(day -> day.startsWith(finalArgument));
    }
}
