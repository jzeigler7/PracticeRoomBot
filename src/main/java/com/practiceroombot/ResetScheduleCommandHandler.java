package com.practiceroombot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Handler for the "reset schedule" command in the Discord bot.
 * This class is responsible for processing the command to reset all room reservations and raids.
 * The command is restricted to users with administrative privileges.
 */
public class ResetScheduleCommandHandler implements ICommandHandler {

    /**
     * Handles the "reset schedule" command.
     * Resets the schedule for all rooms and raids, if the user has admin privileges.
     *
     * @param event The event representing the message received.
     * @param parts The command arguments.
     */
    @Override
    public void handleCommand(MessageReceivedEvent event, String[] parts) {
        String userMention = event.getAuthor().getAsMention(); // Get the mention string for user notification

        // Check if the user has admin access
        if (!userHasAdminAccess(event)) {
            event.getChannel().sendMessage(userMention + " You do not have permission to use this command.").queue();
            return;
        }

        // Reset the schedule
        Schedule.resetCalendar();

        // Send confirmation message with user mention
        event.getChannel().sendMessage(userMention + " All room reservations and raid schedules have been reset.").queue();
    }

    /**
     * Checks if the user who triggered the event has administrative privileges.
     * Implement this method based on your Discord server's role configuration.
     *
     * @param event The event representing the message received.
     * @return True if the user has admin privileges, false otherwise.
     */
    private boolean userHasAdminAccess(MessageReceivedEvent event) {
        // Replace with your method of checking if the user has admin privileges
        return event.getMember() != null && event.getMember().hasPermission(net.dv8tion.jda.api.Permission.ADMINISTRATOR);
    }
}
