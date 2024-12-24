# Georgia Tech Musician's Network Practice Room Reservation Bot

## Overview

This project is a Discord bot designed to manage the reservation process for music practice rooms in the John Lewis Student Center at Georgia Tech. It automates tasks like booking, cancellations, and schedule visualization, while providing tools for server administrators to manage room usage efficiently. The bot is actively deployed in the Georgia Tech Musician's Network Discord server.

## Features

### Core Functionalities

- **Room Reservations**:

  - Users can reserve practice rooms for specified time slots using the `!reserve` command.
  - Automatically validates reservations to prevent conflicts.

- **Cancellations**:

  - Users can cancel existing reservations using the `!cancel` command.

- **Schedule Visualization**:

  - Generates an image of the practice room schedule and shares it in the server with the `!display` command.

- **Recording and Raid Management**:

  - Officers can mark times for recording sessions (`!record`) or special events (`!raid`).

- **Undo Commands**:

  - Admins can remove marked sessions or raids with `!unrecord` and `!unraid` commands, respectively.

### Administrative Tools

- **Reset Schedule**:

  - Resets all room schedules weekly or manually using the `!reset` command.

- **Debugging**:

  - Debugging tools like `!debug` help administrators view technical details like time indices.

### User-Friendly Design

- **Command Prefix**: All commands are prefixed with `!`.
- **Permissions**:
  - Restricts commands like `!raid`, `!reset`, and `!unraid` to users with specific roles or permissions.
- **Clear Feedback**: Error messages guide users on correct command usage.

## Installation

1. **Clone the Repository**:

   ```bash
   git clone <repository_url>
   cd <repository_name>
   ```

2. **Configure the Bot**:

   - Update `config.properties` with your Discord bot token:
     ```
     bot.token=<YOUR_BOT_TOKEN>
     ```

3. **Build and Deploy**:

   - Build the project using your preferred Java build system (e.g., Maven or Gradle).
   - Run the bot:
     ```bash
     java -jar PracticeRoomBot.jar
     ```

## Key Components

### Core Bot Files

- **`BotMain.java`**: Initializes the bot, sets up configuration, and starts the command listener.
- **`CommandListener.java`**: Processes incoming Discord messages and routes valid commands to appropriate handlers.

### Command Handlers

- **`ReserveCommandHandler.java`**:
  - Handles room reservation requests, ensuring validity and scheduling the reservation.
- **`CancelCommandHandler.java`**:
  - Manages reservation cancellations by users.
- **`DisplayCommandHandler.java`**:
  - Generates and sends a visual representation of the room schedules.
- **`RecordCommandHandler.java`**:
  - Schedules recording sessions that block off both rooms.
- **`RaidCommandHandler.java`**:
  - Marks rooms as unavailable for specific time slots due to events.
- **`ResetScheduleCommandHandler.java`**:
  - Clears all reservations and raid marks from the schedule.
- **`UnrecordCommandHandler.java`**:
  - Cancels previously scheduled recording sessions.
- **`UnraidCommandHandler.java`**:
  - Removes raid marks from specified time slots.
- **`WhoHasCommandHandler.java`**:
  - Identifies which user has reserved a specific room for a given time slot.

### Utilities

- **`ConfigLoader.java`**:
  - Loads and manages the bot's configuration properties.
- **`CommandHandlerFactory.java`**:
  - Dynamically maps commands to their respective handlers.
- **`CommandHandlerUtilities.java`**:
  - Provides common utility functions like permission validation.
- **`RealTimeTracker.java`**:
  - Tracks real-world time and maps it to the schedule's indices.
- **`TimeIntegerizer.java`**:
  - Converts human-readable times and days into indices for the schedule.

### Scheduling System

- **`Schedule.java`**:
  - Manages reservations, raids, and recording sessions, ensuring no conflicts.
- **`ScheduleResetHelper.java`**:
  - Automates weekly schedule resets.
- **`ScheduleVisualizer.java`**:
  - Generates a visual representation of the schedule as an image.
- **`ScheduleImageSender.java`**:
  - Sends the schedule image to the appropriate Discord channel.

### Debugging Tools

- **`DebugCommandHandler.java`**:
  - Provides information about the current state of the schedule for troubleshooting.

## Usage

### User Commands

- **Reserve a Room**:
  ```
  !reserve <roomNumber> <day> <startTime> <duration>
  ```
- **Cancel a Reservation**:
  ```
  !cancel <roomNumber> <day> <startTime>
  ```
- **View the Schedule**:
  ```
  !display
  ```
- **Help**:
  ```
  !phelp
  ```

### Officer Commands

- **Record a Session**:
  ```
  !record <day> <startTime> <duration>
  ```
- **Mark a Room as Raided**:
  ```
  !raid <day> <startTime> <duration>
  ```
- **Reset the Schedule**:
  ```
  !reset
  ```
- **Undo Raid Mark**:
  ```
  !unraid <day> <startTime>
  ```
- **Cancel a Recording**:
  ```
  !unrecord <day> <startTime>
  ```
- **Check Room Ownership**:
  ```
  !whohas <roomNumber> <day> <time>
  ```

## Dependencies

- **Java 11 or higher**
- **JDA (Java Discord API)**: For interacting with Discord.
- **SLF4J**: For logging.
- **ImageIO**: For generating schedule visuals.


## Author

**Jacob Zeigler**

