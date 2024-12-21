package com.practiceroombot;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.Arrays;

/**
 * Listener for Discord message events to handle commands.
 * This class extends ListenerAdapter and is responsible for processing
 * messages received in a specific channel as commands.
 */
public class CommandListener extends ListenerAdapter {
    // Prefix to identify commands in messages
    private static final String COMMAND_PREFIX = "!";

    // ID of the channel where commands are accepted
    private final String commandChannelId;

    // Factory to get the appropriate handler for each command
    CommandHandlerFactory commandHandlerFactory;

    /**
     * Constructor for CommandListener.
     *
     * @param commandChannelId The ID of the Discord channel to listen for commands
     */
    public CommandListener(String commandChannelId) {
        this.commandChannelId = commandChannelId;
        this.commandHandlerFactory = new CommandHandlerFactory();
    }

    /**
     * Overrides the onMessageReceived method from ListenerAdapter.
     * Processes messages that start with the command prefix and delegates them to the appropriate handler.
     *
     * @param event The MessageReceivedEvent triggered when a message is received
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore messages from bots or messages not in the designated command channel
        if (event.getAuthor().isBot() || !event.getChannel().getId().equals(commandChannelId)) {
            return;
        }

        String message = event.getMessage().getContentRaw();
        // Process only messages that start with the command prefix
        if (!message.startsWith(COMMAND_PREFIX)) {
            return;
        }

        // Split the message into parts, trim them and filter out empty parts
        String[] rawParts = message.split("\\s+");
        String[] parts = Arrays.stream(rawParts).filter(str -> !str.isEmpty()).toArray(String[]::new);
        String command = parts[0].substring(1).toLowerCase();  // Remove prefix and convert to lower case

        // Get the handler for the extracted command
        ICommandHandler handler = commandHandlerFactory.getHandler(command);
        if (handler != null) {
            try {
                handler.handleCommand(event, parts);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Respond if the command is unknown
            event.getChannel().sendMessage("this command sucks and is not real").queue();
        }
    }
}
