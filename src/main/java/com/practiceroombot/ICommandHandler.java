package com.practiceroombot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.io.IOException;

/**
 * Interface for command handlers in the Discord bot.
 * Defines a contract for handling different types of commands received through Discord messages.
 * Implementing classes must provide an implementation for handling specific commands.
 */
public interface ICommandHandler {

    /**
     * Handles a command received in a Discord message.
     * This method is called when a specific command associated with the implementing handler is received.
     *
     * @param event    The MessageReceivedEvent representing the received Discord message
     * @param parts    The parts of the command, split by spaces
     * @throws IOException If an input/output exception occurs during command handling
     */
    void handleCommand(MessageReceivedEvent event, String[] parts) throws IOException;
}
