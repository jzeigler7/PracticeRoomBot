package com.practiceroombot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Handles the record command for the PracticeRoomBot.
 */
public class RecordCommandHandler implements ICommandHandler {

    @Override
    public void handleCommand(MessageReceivedEvent event, String[] parts) {
        String userMention = event.getAuthor().getAsMention(); // Get the mention string for user notification

        if (CommandHandlerUtilities.lacksOfficerAccess(event)) {
            event.getChannel().sendMessage(userMention + " You do not have permission to use this command.").queue();
            return;
        }

        if (parts.length != 4) {
            event.getChannel().sendMessage(userMention + " Usage: !record <day> <startTime> <duration>").queue();
            return;
        }

        try {
            String day = parts[1];
            String startTime = parts[2];
            double duration = Double.parseDouble(parts[3]);
            int timeIndex = TimeIntegerizer.integerizeTime(startTime, day);
            String message = Schedule.addRecordingSession(timeIndex, duration);

            event.getChannel().sendMessage(userMention + " " + message).queue(); // Include the user mention in the message sent to the channel
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage(userMention + " Invalid number format in command.").queue();
        } catch (Exception e) {
            event.getChannel().sendMessage(userMention + " An unexpected error occurred: " + e.getMessage()).queue();
        }
    }
}
