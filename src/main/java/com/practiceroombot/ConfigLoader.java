package com.practiceroombot;

import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for loading configuration properties.
 * This class is responsible for reading configuration properties from a file,
 * providing a common functionality to access configuration settings.
 */
public class ConfigLoader {

    // Logger for logging errors and information
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    /**
     * Custom exception for configuration loading errors.
     * Extends Exception to provide detailed information about config loading failures.
     */
    public static class ConfigLoadingException extends Exception {
        public ConfigLoadingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Loads properties from a specified file.
     * This method reads a properties file and returns its contents as a Properties object.
     *
     * @param fileName The name of the properties file to be loaded.
     * @return A Properties object containing the loaded properties.
     * @throws ConfigLoadingException If an error occurs during loading the properties.
     */
    public static Properties loadProperties(String fileName) throws ConfigLoadingException {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            Properties prop = new Properties();

            // Check if the properties file is found
            if (input == null) {
                logger.error("Unable to find {}", fileName);
                throw new ConfigLoadingException("Unable to find " + fileName, null);
            }

            // Load properties from the file
            prop.load(input);
            return prop;

        } catch (Exception ex) {
            // Log and rethrow any exception that occurs during property loading
            logger.error("Error loading properties from file {}", fileName, ex);
            throw new ConfigLoadingException("Error loading properties from file " + fileName, ex);
        }
    }
}
