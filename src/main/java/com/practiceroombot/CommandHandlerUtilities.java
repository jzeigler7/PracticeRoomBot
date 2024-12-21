package com.practiceroombot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Arrays;
import java.util.List;

/**
 * Utilities class for command handlers in the Discord bot.
 * This class provides utility methods that are common across various command handlers.
 */
public class CommandHandlerUtilities {

    // List of roles considered as having officer access
    private static final List<String> officerRoles = Arrays.asList("officer", "vp", "admin", "el presidente");

    /**
     * Checks if the member who sent a message has officer access based on their roles.
     *
     * @param event The MessageReceivedEvent containing information about the message and the sender
     * @return true if the member has officer access, false otherwise
     */
    public static boolean lacksOfficerAccess(MessageReceivedEvent event) {
        Member member = event.getMember();

        // Return false if member information is not available
        if (member == null) {
            return true;
        }

        // Check if any of the member's roles match the predefined officer roles
        return member.getRoles().stream()
                .map(Role::getName)
                .noneMatch(roleName -> officerRoles.contains(roleName.toLowerCase()));
    }
}
