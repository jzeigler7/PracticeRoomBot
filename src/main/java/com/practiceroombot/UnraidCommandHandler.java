package com.practiceroombot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.Arrays;

public class UnraidCommandHandler implements ICommandHandler {

    @Override
    public void handleCommand(MessageReceivedEvent event, String[] parts) {
        String userMention = event.getAuthor().getAsMention(); // Get the mention string for user notification

        // Check for officer access
        if (CommandHandlerUtilities.lacksOfficerAccess(event)) {
            event.getChannel().sendMessage(userMention + " Failed: This command may only be used by GTMN admin.").queue();
            return;
        }

        try {
            // Check the number of arguments
            if (parts.length != 3) {
                throw new IllegalArgumentException("Failed: Provide exactly two arguments: day and time.");
            }

            // Validate each individual argument
            String day = validateDay(parts[1]);
            String time = validateTime(parts[2]);

            int timeslotIndex = TimeIntegerizer.integerizeTime(time, day);

            // Remove the raid if present
            if (Schedule.removeRaidInRange(timeslotIndex)) {
                event.getChannel().sendMessage(userMention + " Raid unmarked successfully.").queue();
                ScheduleImageSender.sendScheduleImage(event);
            } else {
                event.getChannel().sendMessage(userMention + " Failed: Ensure timing is correct.").queue();
            }
        } catch (IllegalArgumentException e) {
            event.getChannel().sendMessage(userMention + " Failed: " + e.getMessage()).queue();
        } catch (Exception e) {
            event.getChannel().sendMessage(userMention + " Failed: An unexpected error occurred.").queue();
        }
    }

    boolean isDay(String argument) {
        String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        argument = argument.toLowerCase();
        String finalArgument = argument;
        return Arrays.stream(days).anyMatch(day -> day.startsWith(finalArgument));
    }

    private String validateTime(String argument) throws IllegalArgumentException {
        if (!TimeIntegerizer.isValidTimeFormat(argument)) {
            throw new IllegalArgumentException("Invalid time format: " + argument);
        }
        return argument;
    }

    private String validateDay(String argument) throws IllegalArgumentException {
        if (!isDay(argument)) {
            throw new IllegalArgumentException("Invalid day: " + argument);
        }
        return argument;
    }
}
