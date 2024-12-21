package com.practiceroombot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class WhoHasCommandHandler implements ICommandHandler {

    @Override
    public void handleCommand(MessageReceivedEvent event, String[] parts) {
        Member member = event.getMember();
        if (member != null) {
            List<Role> roles = member.getRoles();
            boolean hasAccess = roles.stream().anyMatch(role -> role.getName().equalsIgnoreCase("practice room access"));

            if (!hasAccess) {
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + "Failed: You do not have permission to use this command.").queue();
                return;
            }
        }

        // Check the correct number of parameters is provided
        if (parts.length != 4) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + "Failed: Usage: !whohas <roomNumber> <day> <time>").queue();
            return;
        }

        try {
            int roomNumber = Integer.parseInt(parts[1]);
            String day = parts[2];
            String time = parts[3];
            int timeIndex = TimeIntegerizer.integerizeTime(time, day);

            String reservation = Schedule.getReservation(roomNumber, timeIndex);
            String response = reservation != null ? reservation + " has room " + roomNumber + " reserved on " + day + " " +
                    "at " + time + ".": "Room " + roomNumber + " is vacant at " + time + " on " + day + ".";

            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " " + response).queue();
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + "Failed: Invalid number or time format.").queue();
        } catch (IllegalArgumentException e) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + "Failed: " + e.getMessage()).queue();
        } catch (Exception e) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + "Failed: An unexpected error occurred: " + e.getMessage()).queue();
        }
    }
}
