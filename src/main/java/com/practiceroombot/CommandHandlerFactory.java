package com.practiceroombot;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating command handlers in the Discord bot.
 * This class maintains a mapping of command strings to their respective
 * ICommandHandler implementations and provides a method to retrieve them.
 */
public class CommandHandlerFactory {
    // Map to hold the associations between command strings and their handlers
    private final Map<String, ICommandHandler> handlers;

    /**
     * Constructor for CommandHandlerFactory.
     * Initializes the handler map and associates command strings with their respective handlers.
     */
    public CommandHandlerFactory() {
        handlers = new HashMap<>();

        // Initialize command handlers
        handlers.put("reserve", new ReserveCommandHandler());
        handlers.put("cancel", new CancelCommandHandler());
        handlers.put("display", new DisplayCommandHandler());
        handlers.put("raid", new RaidCommandHandler());
        handlers.put("unraid", new UnraidCommandHandler());
        handlers.put("phelp", new HelpCommandHandler());
        handlers.put("record", new RecordCommandHandler());
        handlers.put("unrecord", new UnrecordCommandHandler());
        handlers.put("reset", new ResetScheduleCommandHandler());
        handlers.put("whohas", new WhoHasCommandHandler());
        handlers.put("debug", new DebugCommandHandler());
    }

    /**
     * Retrieves the command handler associated with a given command string.
     *
     * @param command The command string for which the handler is to be retrieved
     * @return The ICommandHandler associated with the given command, or null if no handler exists
     */
    public ICommandHandler getHandler(String command) {
        return handlers.get(command);
    }
}
