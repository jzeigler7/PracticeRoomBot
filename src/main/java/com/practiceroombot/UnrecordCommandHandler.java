package com.practiceroombot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.Arrays;

public class UnrecordCommandHandler implements ICommandHandler {

    @Override
    public void handleCommand(MessageReceivedEvent event, String[] parts) {
        String userMention = event.getAuthor().getAsMention();  // Get the mention string for user notification

        // Check if the user has officer access
        if (CommandHandlerUtilities.lacksOfficerAccess(event)) {
            event.getChannel().sendMessage(userMention + " Failed: This command may only be used by GTMN admin.").queue();
            return;
        }

        try {
            if (parts.length != 3) {
                throw new IllegalArgumentException(userMention + " Failed: Provide exactly two arguments: day and time.");
            }

            String day = validateDay(parts[1]);
            String time = validateTime(parts[2]);

            int timeIndex = TimeIntegerizer.integerizeTime(time, day);

            // Cancel the recording session if it is found
            if (Schedule.isRecordingSession(timeIndex)) {
                Schedule.cancelRecordingSession(timeIndex);
                event.getChannel().sendMessage(userMention + " Recording session cancelled successfully.").queue();
                ScheduleImageSender.sendScheduleImage(event);
            } else {
                event.getChannel().sendMessage(userMention + " Failed: Unable to identify recording session.").queue();
            }
        } catch (IllegalArgumentException e) {
            event.getChannel().sendMessage(userMention + " Error: " + e.getMessage()).queue();
        } catch (Exception e) {
            event.getChannel().sendMessage(userMention + " Failed: An unexpected error occurred.").queue();
        }
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

    boolean isDay(String argument) {
        String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        argument = argument.toLowerCase();
        String finalArgument = argument;
        return Arrays.stream(days).anyMatch(day -> day.startsWith(finalArgument));
    }
}