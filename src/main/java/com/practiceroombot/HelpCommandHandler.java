package com.practiceroombot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Handler for the "help" command in the Discord bot.
 * This class implements ICommandHandler and is responsible for processing the
 * help command, which involves sending a message with a list of available commands.
 */
public class HelpCommandHandler implements ICommandHandler {

    /**
     * Handles the "help" command, sending a message with a list of available commands.
     * This method constructs a help message and sends it to the Discord channel.
     *
     * @param event    The message event that triggered this command
     * @param parts    Array of strings representing the parts of the command (not used in this handler)
     */
    @Override
    public void handleCommand(MessageReceivedEvent event, String[] parts) {
        // Construct the help message with a list of available commands
        String helpMessage = """
                Here are the available commands:
                !reserve <roomNumber> <day> <startTime> <duration>: To reserve a room.
                !cancel <roomNumber> <day> <startTime>: To cancel a reservation.
                !display: To display the schedule.
                !whohas <roomNumber> <day> <time>: To display who is using a room at a given time.
                !raid <day> <startTime> <duration>: To mark equipment as removed (Officers only).
                !unraid <day> <startTime>: To remove raid mark (Officers only).
                !record <day> <startTime> <duration>: To add a recording session (Officers only).
                !unrecord <day> <startTime>: To cancel a recording session (Officers only).
                !phelp: To display this message.""";

        // Send the help message to the Discord channel
        event.getChannel().sendMessage(helpMessage).queue();
    }
}
