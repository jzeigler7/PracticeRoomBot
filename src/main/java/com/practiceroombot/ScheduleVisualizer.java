package com.practiceroombot;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.practiceroombot.Schedule.isRecordingSession;

/**
 * Generates a visual representation of the schedule as an image.
 * Different colors are used to represent different states of reservations.
 */
public class ScheduleVisualizer {

    private static final int BLOCK_SIZE = 40;
    private static final int LABEL_HEIGHT = 40;
    private static final int LABEL_WIDTH = 120;
    private static final int GRID_LINE_WIDTH = 2; // Increased grid line width

    // Color definitions for different types of reservations
    private static final Color ORANGE = new Color(255, 165, 0); // Color for affected by club events
    private static final Color GREEN = new Color(0, 128, 0);    // Color for recording sessions
    private static final Color RED = new Color(255, 0, 0);      // Color for room 1 reserved
    private static final Color YELLOW = new Color(255, 255, 0); // Color for room 2 reserved
    private static final Color BLUE = new Color(0, 0, 255);     // Color for both rooms reserved
    private static final Color PURPLE = new Color(128, 0, 128); // Color for user's reservation in room 1
    private static final Color PINK = new Color(255, 192, 203); // Color for user's reservation in room 2

    private static final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon"};

    /**
     * Generates an image representing the current schedule.
     * This method creates a BufferedImage where different colors represent different states
     * of reservations, including the requesting user's reservations.
     *
     * @param requestingUser The user for whom the schedule is being visualized.
     * @return A BufferedImage representing the current state of the schedule.
     */
    public static BufferedImage generateScheduleImage(String requestingUser) {
        // Swap width and height to transpose the schedule
        int width = 48 * BLOCK_SIZE + LABEL_WIDTH; // For time labels and time blocks
        int height = 9 * BLOCK_SIZE; // For day labels and day blocks

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setFont(new Font("Arial", Font.PLAIN, 20));

        drawDayLabels(graphics);
        drawTimeLabels(graphics);

        // Fill the blocks with transposed axes
        for (int day = 0; day < 8; day++) {
            for (int hour = 0; hour < 48; hour++) {
                int index = determineIndex(day, hour);
                if (isValidTimeBlock(day, hour)) {
                    int color;
                    if (isBlackoutPeriod(day, hour)) {
                        color = Color.BLACK.getRGB();
                    } else {
                        color = determineColor(index, requestingUser);
                    }
                    // Transposed filling of blocks
                    fillBlock(graphics, day, hour, color);
                }
            }
        }

        graphics.dispose();
        return image;
    }

    /**
     * Determines whether a given day and hour fall within a blackout period.
     * A blackout period is defined as a time when certain activities, such as reservations,
     * are restricted. Specifically, this method identifies blackout periods for two scenarios:
     * - The first Monday between 12 AM and 7:30 PM (represented by day 0 and hours less than 15).
     * - The second Monday between 7:30 PM and 12 AM (represented by day 7 and hours greater than or equal to 39).
     *
     * @param day  The day of the week, where 0 represents the first Monday and 7 represents the second Monday.
     * @param hour The hour of the day in a 48-hour format (0-47), where each hour represents a half-hour block.
     * @return true if the specified day and hour fall within a blackout period, false otherwise.
     */
    private static boolean isBlackoutPeriod(int day, int hour) {
        // Logic to determine if it's a blackout period
        // For the first Monday between 12 AM and 7:30 PM
        if (day == 0 && hour < 39) return true;
        // For the second Monday between 7:30 PM and 12 AM
        return day == 7 && hour >= 39;
    }


    /**
     * Draws the labels for each day at the left side of the image.
     *
     * @param graphics The Graphics2D object used to draw on the image.
     */
    private static void drawDayLabels(Graphics2D graphics) {
        graphics.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics fm = graphics.getFontMetrics();

        for (int i = 0; i < DAYS.length; i++) {
            String day = DAYS[i];

            // Calculate x position to be before the start of the blocks
            int xPosition = 5; // You can adjust this value to position the labels further left if needed

            // Calculate y position to be vertically centered in each day's block
            int yPosition = LABEL_HEIGHT + (i * ScheduleVisualizer.BLOCK_SIZE) + (ScheduleVisualizer.BLOCK_SIZE / 2) + (fm.getAscent() / 2) - (fm.getDescent() / 2);

            graphics.drawString(day, xPosition, yPosition);
        }
    }


    /**
     * Draws the labels for each hour at the top of the image.
     *
     * @param graphics The Graphics2D object used to draw on the image.
     */
    private static void drawTimeLabels(Graphics2D graphics) {
        graphics.setFont(new Font("Arial", Font.PLAIN, 20));
        for (int i = 0; i < 24; i++) {
            // Horizontal time labels at the top, displaying only hour numbers
            String timeLabel = String.format("%d", (i == 0 || i == 12) ? 12 : i % 12); // 12, 1, 2,...11, 12, 1, 2,...11
            int xPos = ScheduleVisualizer.LABEL_WIDTH + i * 2 * ScheduleVisualizer.BLOCK_SIZE; // Position at the start of each hour
            int yPos = LABEL_HEIGHT / 2;
            graphics.drawString(timeLabel, xPos - graphics.getFontMetrics().stringWidth(timeLabel) / 2, yPos);
        }
    }

    /**
     * Fills a block with a specific color based on the reservation status.
     *
     * @param graphics The Graphics2D object used to draw on the image.
     * @param day      The day (row) on which the block is located.
     * @param hour     The hour (column) on which the block is located.
     * @param color    The color to fill the block with.
     */
    private static void fillBlock(Graphics2D graphics, int day, int hour, int color) {
        // Swap x and y in filling logic
        int x = LABEL_WIDTH + hour * BLOCK_SIZE;
        int y = (day + 1) * BLOCK_SIZE;

        int blockSizeWithGap = BLOCK_SIZE - GRID_LINE_WIDTH;

        graphics.setColor(new Color(color));
        graphics.fillRect(x, y, blockSizeWithGap, blockSizeWithGap);

        graphics.setColor(Color.BLACK);
        graphics.drawRect(x, y, blockSizeWithGap, blockSizeWithGap);
    }

    /**
     * Determines if a given time block is valid and should be displayed.
     *
     * @param day  The day to check.
     * @param hour The hour to check.
     * @return true if the time block is valid, false otherwise.
     */
    private static boolean isValidTimeBlock(int day, int hour) {
        return day >= 0 && day < 8 && hour >= 0 && hour < 48;
    }

    /**
     * Determines the color to use for a specific time block based on the reservation status.
     *
     * @param index          The index of the time block in the schedule.
     * @param requestingUser The user for whom the schedule is being visualized.
     * @return The color to use for the block.
     */
    private static int determineColor(int index, String requestingUser) {
        // Check bounds of the arrays
        if (index < 0 || index >= Schedule.pr1schedule.length || index >= Schedule.raids.length) {
            return Color.WHITE.getRGB(); // Return a default color for out-of-bounds index
        }

        String reservation1 = Schedule.pr1schedule[index];
        String reservation2 = Schedule.pr2schedule[index];

        // Check if the timeslot is occupied by a recording session
        if (isRecordingSession(index)) {
            return GREEN.getRGB();
        }
        if (requestingUser.equals(reservation1) && !requestingUser.equals(reservation2)) {
            return PURPLE.getRGB();
        }
        if (requestingUser.equals(reservation2) && !requestingUser.equals(reservation1)) {
            return PINK.getRGB();
        }
        if (reservation1 != null && reservation2 != null) {
            return BLUE.getRGB();
        }
        if (reservation1 != null) {
            return RED.getRGB();
        }
        if (reservation2 != null) {
            return YELLOW.getRGB();
        }
        if (Schedule.raids[index] && Schedule.pr1schedule[index] == null) {
            return ORANGE.getRGB();
        }
        return Color.WHITE.getRGB();
    }

    /**
     * Calculates the index for a specific day and hour in the schedule.
     *
     * @param day  The day of the week.
     * @param hour The hour of the day in half-hour blocks (0-47).
     * @return The calculated index in the schedule.
     */
    private static int determineIndex(int day, int hour) {
        int correctedHour = hour - 39; // Adjust hour to start from 7:30 PM
        if (correctedHour < 0) {
            correctedHour += 48; // Wrap around to the previous day's hours if before 7:30 PM
            day--;
        }
        if (day < 0) day += 8; // Wrap around to the previous week's last day if before first day
        return day * 48 + correctedHour;
    }
}
