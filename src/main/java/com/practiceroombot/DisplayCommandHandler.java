package com.practiceroombot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Handler for the "display" command in the Discord bot.
 * This class implements ICommandHandler and is responsible for processing the
 * display command, which involves visualizing and sending the schedule image.
 */
public class DisplayCommandHandler implements ICommandHandler {

    /**
     * Handles the "display" command, generating and sending a schedule image.
     * Converts a BufferedImage of the schedule to a byte array and sends it as an attachment.
     *
     * @param event    The message event that triggered this command
     * @param parts    Array of strings representing the parts of the command
     * @throws IOException If an error occurs during image processing or sending
     */
    @Override
    public void handleCommand(MessageReceivedEvent event, String[] parts) throws IOException {
        String userMention = event.getAuthor().getAsMention(); // Get the mention string for user notification
        try {
            // Generate an image representation of the schedule
            BufferedImage scheduleImage = ScheduleVisualizer.generateScheduleImage(event.getAuthor().getName());

            // Convert the BufferedImage to an InputStream or directly to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(scheduleImage, "png", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

            // Send the image as a file attachment in the Discord channel
            event.getChannel().sendFiles(FileUpload.fromData(inputStream, "schedule.png")).queue(message -> {
                event.getChannel().sendMessage(userMention + " Here is the current schedule:").queue();
            });
        } catch (IOException e) {
            event.getChannel().sendMessage(userMention + " An error occurred while processing the schedule image.").queue();
            throw e; // Re-throw the exception after handling
        }
    }
}

