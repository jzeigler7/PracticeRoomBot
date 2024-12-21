package com.practiceroombot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DebugCommandHandler implements ICommandHandler {

    @Override
    public void handleCommand(MessageReceivedEvent event, String[] parts) {
        try {
            // Check if the command includes the debug code
            if (parts.length < 2) {
                throw new IllegalArgumentException("Please provide a debug code.");
            }

            int debugCode = Integer.parseInt(parts[1]);

            switch (debugCode) {
                case 1:
                    // Debug code 1: Output the current time index
                    int currentTimeIndex = RealTimeTracker.getCurrentTimeIndex();
                    event.getChannel().sendMessage("Current Time Index: " + currentTimeIndex).queue();
                    break;
                case 2:
                    // Debug code 2: Output the integerized index of a given time and day
                    if (parts.length < 4) {
                        throw new IllegalArgumentException("Please provide both day and time for debug code 2.");
                    }
                    String day = parts[2];
                    String time = parts[3];
                    int timeIndex = TimeIntegerizer.integerizeTime(time, day);
                    event.getChannel().sendMessage("Integerized Time Index for " + day + " " + time + ": " + timeIndex).queue();
                    break;
                default:
                    event.getChannel().sendMessage("Invalid debug code.").queue();
                    break;
            }
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("Debug code must be a valid integer.").queue();
        } catch (IllegalArgumentException e) {
            event.getChannel().sendMessage(e.getMessage()).queue();
        } catch (Exception e) {
            event.getChannel().sendMessage("An unexpected error occurred: " + e.getMessage()).queue();
        }
    }
}
