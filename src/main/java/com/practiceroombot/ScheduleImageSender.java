package com.practiceroombot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ScheduleImageSender {

    /**
     * Sends the current schedule image to the specified Discord channel.
     * @param event The message event that triggered the command.
     * @throws IOException If an error occurs during image processing or sending.
     */
    public static void sendScheduleImage(MessageReceivedEvent event) throws IOException {
        // Generate an image representation of the schedule
        BufferedImage scheduleImage = ScheduleVisualizer.generateScheduleImage(event.getAuthor().getName());

        // Convert the BufferedImage to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(scheduleImage, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

        // Send the image as a file attachment in the Discord channel
        event.getChannel().sendFiles(FileUpload.fromData(inputStream, "schedule.png")).queue();
    }
}
