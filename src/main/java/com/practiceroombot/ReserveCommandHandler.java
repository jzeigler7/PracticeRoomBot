package com.practiceroombot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class ReserveCommandHandler implements ICommandHandler {

    @Override
    public void handleCommand(MessageReceivedEvent event, String[] parts) {
        Member member = event.getMember();
        if (member != null) {
            List<Role> roles = member.getRoles();
            boolean hasAccess = roles.stream().anyMatch(role -> role.getName().equalsIgnoreCase("practice room access"));

            if (!hasAccess) {
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have permission to use this command.").queue();
                return;
            }
        }

        if (parts.length != 5) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Failed: Usage: !reserve <roomNumber> <day> <startTime> <duration>").queue();
            return;
        }

        try {
            int roomNumber = Integer.parseInt(parts[1]);
            String day = parts[2];
            String startTime = parts[3];
            double duration = Double.parseDouble(parts[4]);
            int startSlotIndex = TimeIntegerizer.integerizeTime(startTime, day);
            int currentSlotIndex = RealTimeTracker.getCurrentTimeIndex();

            if (startSlotIndex < currentSlotIndex) {
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Failed: Cannot reserve time in the past.").queue();
                return;
            }

            String response = Schedule.addReservation(roomNumber, event.getAuthor().getName(), startSlotIndex, duration);
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " " + response).queue();
            if (response.startsWith("Congrats")) {
                ScheduleImageSender.sendScheduleImage(event);
            }

        } catch (NumberFormatException e) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Failed: Invalid number format in command.").queue();
        } catch (Exception e) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Failed: An unexpected error occurred: " + e.getMessage()).queue();
        }
    }
}
