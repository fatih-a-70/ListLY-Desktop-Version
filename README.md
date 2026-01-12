# ListLY Desktop - JavaFX To-Do List Application

A comprehensive desktop task management application built with JavaFX and SQLite, converted from an Android app to provide a cross-platform experience.

## Features

### Core Functionality
- **Multiple List Types**: Checkbox, Wishlist, Plain List, Notes, and Memo
- **Category Organization**: Group lists into categories for better organization
- **Task Management**: Create, edit, delete, and track tasks with duration monitoring
- **Focus Mode (Stopwatch)**: 
  - Three modes: Stopwatch, Countdown Forward, Countdown Backward
  - Preset timers (10min, 30min, 1hr, 3hr)
  - Custom timer settings
  - Focus session history tracking
- **Reminders**: Set up to 4 task reminders with customizable times
- **Sorting Options**: Sort by Alphabetical, Recent, Oldest, or List Style
- **Customization**: 
  - Multiple background themes
  - Custom text colors and fonts for lists and tasks
  - Personalized username

### Data Persistence
- **SQLite Database**: All data stored locally in `listly.db`
- **Automatic Duration Tracking**: Tasks and lists track time spent
- **Real-time Sync**: Changes saved immediately to database

## Project Structure

```
src/main/java/org/example/listly/
├── HelloApplication.java          # Main application entry point
├── MainController.java            # Home screen controller
├── CheckboxscreenController.java  # Task list view controller
├── StopwatchController.java       # Focus mode/stopwatch controller
├── RemindersController.java       # Reminders management controller
├── Database.java                  # SQLite database initialization
├── CategoryDao.java               # Category & List database operations
├── TaskDao.java                   # Task database operations
├── ReminderDao.java               # Reminder database operations
├── FocusSessionDao.java           # Focus session database operations
├── CategoryItem.java              # Category model
├── ListItem.java                  # List model
├── TaskItem.java                  # Task model
├── ReminderItem.java              # Reminder model
├── FocusSession.java              # Focus session model
├── ListStyle.java                 # Enum for list types
├── SortMode.java                  # Enum for sort options
├── ListSorter.java                # List sorting utilities
├── TaskCell.java                  # Custom cell renderer for tasks
├── Dates.java                     # Date/time formatting utilities
├── Dialogs.java                   # Dialog helper methods
└── Prefs.java                     # User preferences management

src/main/resources/org/example/listly/
├── activity_main.fxml             # Main screen layout
├── checkboxscreen.fxml            # Task list screen layout
├── stopwatch.fxml                 # Stopwatch screen layout
├── reminders.fxml                 # Reminders screen layout
├── homebox.fxml                   # Category box component
├── item_list.fxml                 # List item component
└── item_checkbox.fxml             # Task item component
```

## Database Schema

### Categories Table
- `id` (TEXT PRIMARY KEY): Unique category identifier
- `name` (TEXT): Category name
- `created_at` (INTEGER): Creation timestamp
- `updated_at` (INTEGER): Last update timestamp

### Lists Table
- `id` (TEXT PRIMARY KEY): Unique list identifier
- `category_id` (TEXT): Foreign key to categories
- `title` (TEXT): List title
- `style` (TEXT): List style (CHECKBOX, WISHLIST, PLAIN, NOTE, MEMO)
- `theme_image` (TEXT): Theme background
- `text_color` (INTEGER): Text color (ARGB format)
- `font_size` (REAL): Font size in SP
- `font_style` (TEXT): Font style (NORMAL, BOLD, ITALIC)
- `created_at` (INTEGER): Creation timestamp
- `updated_at` (INTEGER): Last update timestamp
- `total_duration_ms` (INTEGER): Total time spent on tasks

### Tasks Table
- `id` (TEXT PRIMARY KEY): Unique task identifier
- `list_id` (TEXT): Foreign key to lists
- `name` (TEXT): Task name
- `checked` (INTEGER): Checked state (0/1)
- `start_time` (INTEGER): Task start timestamp
- `end_time` (INTEGER): Task completion timestamp
- `text_color` (INTEGER): Text color
- `font_style` (TEXT): Font style
- `position` (INTEGER): Display order

### Reminders Table
- `id` (INTEGER PRIMARY KEY): Auto-increment ID
- `task_name` (TEXT): Reminder task name
- `hour` (INTEGER): Reminder hour (24h format)
- `minute` (INTEGER): Reminder minute
- `enabled` (INTEGER): Enabled state (0/1)
- `reminder_key` (TEXT UNIQUE): Unique identifier

### Focus Sessions Table
- `id` (INTEGER PRIMARY KEY): Auto-increment ID
- `mode` (TEXT): Focus mode type
- `duration_planned` (INTEGER): Planned duration (ms)
- `duration_done` (INTEGER): Actual duration (ms)
- `date_ms` (INTEGER): Session timestamp

### Preferences Table
- `key` (TEXT PRIMARY KEY): Preference key
- `value` (TEXT): Preference value

## Running the Application

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- JavaFX 21

### Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn javafx:run
```

### Creating Executable
```bash
# Create executable JAR
mvn clean package

# Run the JAR
java -jar target/ListLY-1.0-SNAPSHOT.jar
```

## Usage Guide

### Creating Categories and Lists
1. Click the **+** button on the main screen
2. Select list type (Checkbox, Wishlist, etc.)
3. Choose existing category or create new one
4. Enter list name

### Managing Tasks
1. Double-click a list card to open it
2. Click **+** button to add tasks
3. Check/uncheck tasks (for checkbox lists)
4. Double-click tasks to edit/delete or change appearance

### Focus Mode
1. Click the **stopwatch** button on main screen
2. Select focus mode (Stopwatch/Countdown)
3. Choose preset time or set custom duration
4. Click **Start** to begin session
5. View history with **Focus Sessions History** button

### Setting Reminders
1. Click the **alarm clock** button on main screen
2. Double-click task names to edit
3. Double-click times to set (HH:MM format)
4. Toggle switches to enable/disable reminders

### Customization
1. Click the **more** button (three dots)
2. **Change Background**: Select theme
3. **Set Username**: Personalize welcome message

### Sorting
1. Click the sort dropdown (default: "All Lists")
2. Choose: Alphabetical, Recent, Oldest, or List Style

## Key Features Comparison

| Feature | Android App | Desktop App (JavaFX) |
|---------|-------------|----------------------|
| Database | Firebase Firestore | SQLite (local) |
| UI Framework | Android XML + RecyclerView | JavaFX FXML + ListView |
| Notifications | AlarmManager + NotificationManager | JavaFX Timeline (in-app) |
| Data Persistence | Cloud-based | Local file-based |
| Navigation | Activities + Intents | Controllers + Stage |
| Styling | Android Themes | JavaFX CSS |

## Technical Implementation Notes

### SQLite Integration
- Uses `org.xerial:sqlite-jdbc:3.47.1.0`
- Database file: `listly.db` in application directory
- Connection pooling via `Database.get().getConnection()`

### Duration Tracking
- Tasks track `startTime` and `endTime`
- Duration automatically calculated on task completion
- Aggregated to list level, then category level
- Formatted using `Dates.formatDuration()`

### List Styles
Each list style renders tasks differently:
- **CHECKBOX**: Shows checkbox with toggle functionality
- **WISHLIST**: Displays with ✨ sparkle icon
- **PLAIN**: Simple bullet point list (•)
- **NOTE**: Plain text for note-taking
- **MEMO**: Similar to notes with memo formatting

### Focus Mode
Three countdown modes:
1. **Stopwatch**: Counts up from 00:00:00
2. **Countdown Forward**: Counts up to target, beeps when complete
3. **Countdown Backward**: Counts down from target to 00:00:00

### Preferences
Stored using Java Preferences API (`java.util.prefs.Preferences`)
- Username: `Prefs.get("username", "")`
- Background theme: `Prefs.get("main_bg", "Default")`

## Dependencies

```xml
<dependencies>
    <!-- JavaFX -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.6</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>21.0.6</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-media</artifactId>
        <version>21.0.6</version>
    </dependency>
    
    <!-- SQLite -->
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.47.1.0</version>
    </dependency>
</dependencies>
```

## Troubleshooting

### Database Issues
- **Database locked**: Ensure only one instance is running
- **Missing tables**: Delete `listly.db` and restart (creates fresh schema)

### JavaFX Issues
- **FXML load errors**: Check fx:id attributes match controller @FXML fields
- **CSS not applying**: Verify `.setStyle()` calls use proper CSS syntax

### Performance
- Large task lists (1000+): Consider pagination in ListView
- Frequent saves: Batch operations using transactions

## Future Enhancements
- Cloud sync (Firebase/custom backend)
- Export/import (JSON, CSV)
- Task priorities and tags
- Calendar view
- Pomodoro timer integration
- Dark mode
- Multi-language support
- Task attachments

## License
This is a personal project converted from Android to JavaFX desktop application.

## Credits
Original Android app design and concept converted to JavaFX desktop implementation.
