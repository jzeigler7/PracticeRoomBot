package com.practiceroombot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Handler for the "raid" command in the Discord bot.
 */
public class RaidCommandHandler implements ICommandHandler {

    @Override
    public void handleCommand(MessageReceivedEvent event, String[] parts) {
        String userMention = event.getAuthor().getAsMention(); // Get the mention string for user notification

        if (parts.length != 4 || CommandHandlerUtilities.lacksOfficerAccess(event)) {
            event.getChannel().sendMessage(userMention + " Usage: !raid <day> <startTime> <duration> (Officers only)").queue();
            return;
        }

        try {
            String day = parts[1];
            String startTime = parts[2];
            double duration = Double.parseDouble(parts[3]);
            String message = Schedule.addRaid(TimeIntegerizer.integerizeTime(startTime, day), duration);

            event.getChannel().sendMessage(userMention + " " + message).queue(); // Include the user mention in the message sent to the channel
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage(userMention + " Invalid number format in command.").queue();
        }
    }
}
