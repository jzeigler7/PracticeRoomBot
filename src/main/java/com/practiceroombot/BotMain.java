package com.practiceroombot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Main class for the PracticeRoomBot.
 * This class is responsible for initializing and running the Discord bot.
 */
public class BotMain {

    // Logger for this class, using SLF4J LoggerFactory
    private static final Logger logger = LoggerFactory.getLogger(BotMain.class);

    /**
     * The main entry point of the PracticeRoomBot.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        try {
            ScheduleResetTask.startResetScheduleTimer();
            // Load configuration properties for the bot
            Properties properties = loadConfiguration();

            // Validate the bot token obtained from the properties
            String botToken = validateToken(properties);

            // Build the JDABuilder with the bot token
            JDABuilder builder = JDABuilder.createDefault(botToken);

            // Initialize and start the bot
            initializeAndStartBot(builder);
        } catch (Exception e) {
            // Log any exceptions that occur during initialization
            logger.error("Error during bot initialization: ", e);
        }
    }



    /**
     * Loads the configuration properties from a file.
     *
     * @return Properties loaded from the configuration file.
     * @throws Exception if the configuration file cannot be loaded.
     */
    static Properties loadConfiguration() throws Exception {
        // Load the properties file named config.properties
        Properties properties = ConfigLoader.loadProperties("config.properties");

        // Check if properties are successfully loaded
        if (properties.isEmpty()) {
            throw new Exception("Unable to load config.properties or the file is empty.");
        }

        return properties;
    }



    /**
     * Validates the bot token present in the properties.
     *
     * @param properties The properties object containing the bot token.
     * @return The validated bot token.
     * @throws Exception if the bot token is not specified or is empty.
     */
    static String validateToken(Properties properties) throws Exception {
        // Retrieve the bot token from properties
        String botToken = properties.getProperty("bot.token");

        // Check if the bot token is valid
        if (botToken == null || botToken.trim().isEmpty()) {
            throw new Exception("Bot token not specified in config.properties.");
        }

        return botToken;
    }

    /**
     * Initializes and starts the Discord bot.
     *
     * @param builder The JDABuilder used to build the JDA instance.
     */
    static void initializeAndStartBot(JDABuilder builder) {
        try {
            // Set the bot's activity and enable necessary intents
            builder.setActivity(Activity.playing("Type !phelp for commands"))
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new CommandListener("1166092015465922693"));

            // Build the JDA instance and wait for it to be ready
            JDA jda = builder.build();
            jda.awaitReady();

            // Log the status indicating the bot is running
            logger.info("Bot is running!");
        } catch (Exception e) {
            // Log any exceptions that occur during bot initialization
            logger.error("Error during bot initialization: ", e);
        }
    }
}
