# ListLY Desktop - Quick Start Guide

## Installation and Setup

### Step 1: Prerequisites
Ensure you have:
- **Java 21+** installed
- **Maven 3.6+** installed
- **Git** (to clone if needed)

Check versions:
```bash
java --version
mvn --version
```

### Step 2: Build the Project
```bash
cd c:\Users\Hp\IdeaProjects\demo\ListLY

# Clean and build
mvn clean install
```

### Step 3: Run the Application
```bash
mvn javafx:run
```

## First Launch

### Welcome Screen
- Enter your username when prompted
- Choose a background theme
- You'll see the main home screen

### Creating Your First List

1. **Click the `+` button** at the bottom
2. **Select list type**:
   - **Checkbox**: For to-do lists with checkable items
   - **Wishlist**: For items you wish to have/do
   - **Plain List**: Simple bullet point list
   - **Notes**: Free-form note taking
   - **Memo**: Short memos

3. **Choose category**:
   - Select existing category OR
   - Choose "New Category" to create one

4. **Enter list name** and click Create

### Adding Tasks

1. **Double-click** the list card you just created
2. In the task screen, click the **`+`** button
3. **Enter task name** and click Add
4. **Check/uncheck** tasks to mark complete (for checkbox lists)

### Task Features

**Double-click any task** to access options:
- ✏️ **Edit**: Change task name
- 🗑️ **Delete**: Remove task
- ⏱️ **Duration**: View time spent
- 🎨 **Color**: Change text color
- 📝 **Font**: Adjust font style

## Quick Features Guide

### Using Focus Mode (Stopwatch)
1. Click **stopwatch icon** on main screen
2. Choose mode:
   - **Focus Mode** button → Select mode type
   - **Stopwatch**: Simple timer counting up
   - **Countdown Forward**: Count up to target
   - **Countdown Backward**: Count down from target

3. Set duration:
   - Use preset buttons (10min, 30min, 1hr, 3hr)
   - **Long-press** preset buttons to customize
   - Click **Set timer** for custom duration

4. **Start** to begin, **Stop** to pause, **Reset** to clear

5. View **Focus Sessions History** to see all completed sessions

### Setting Reminders
1. Click **alarm clock icon** on main screen
2. You have 4 reminder slots
3. For each reminder:
   - **Double-click task name** to edit
   - **Double-click time** to set (format: HH:MM in 24h)
   - **Toggle switch** to enable/disable

4. Click **Save** to activate reminders

### Sorting Your Lists
1. Click the **sort dropdown** (shows "All Lists" by default)
2. Choose sorting:
   - **Alphabetical**: A-Z by name
   - **Recent**: Newest first
   - **Oldest**: Oldest first
   - **List Style**: Grouped by type (Checkbox, Wishlist, etc.)

### Customizing Lists

**List Options** (double-click list title):
- ✏️ **Edit Name**: Rename the list
- 🎨 **Text Color**: Change title color (Default, Red, Blue, Green, Black)
- 📝 **Font**: Adjust font style and size
- 🗑️ **Delete**: Remove list permanently
- ⏱️ **Duration**: View total time spent

**Category Options** (double-click category name):
- ✏️ **Edit Category Name**: Rename
- 🗑️ **Delete Category**: Remove (deletes all lists inside)
- ⏱️ **Duration**: View total time for all lists in category

### Changing Themes
1. Click **three dots icon** (more button)
2. Select **Change Background**
3. Choose from:
   - Default
   - Blue
   - Green
   - Purple
   - Dark
   - Light

## Common Workflows

### Daily Task Management
```
1. Morning: Open ListLY
2. Check today's reminders
3. Open your "Daily Tasks" list
4. Check off completed tasks
5. Add new tasks as they come up
6. Use Focus Mode for deep work sessions
```

### Project Planning
```
1. Create new category: "Project Alpha"
2. Create lists:
   - "Tasks" (Checkbox)
   - "Ideas" (Notes)
   - "Resources" (Wishlist)
3. Populate each list
4. Track progress by checking tasks
5. View duration to see time invested
```

### Shopping List
```
1. Create "Shopping" list (Wishlist or Plain)
2. Add items you need to buy
3. Take ListLY with you (on desktop!)
4. Check items as you shop
```

## Keyboard Shortcuts

Currently the app uses mouse/click navigation. Future versions may add:
- `Ctrl+N`: New list
- `Ctrl+S`: Save (auto-save is on)
- `Ctrl+Q`: Quit
- `F5`: Refresh view

## Tips and Tricks

### Efficient Task Entry
- Keep task names short and actionable
- Use verbs: "Call John" not "John"
- Break large tasks into smaller subtasks

### Duration Tracking
- Start tasks when you begin work
- Check them when done
- View duration to understand time spent
- Use for time management insights

### Focus Mode Best Practices
- Use **Pomodoro technique**: 25min work, 5min break
- Customize presets to your workflow
- Review history to track productivity

### Organization
- Create categories by:
  - **Project** (Work, Personal, School)
  - **Area** (Home, Office, Errands)
  - **Time** (Daily, Weekly, Monthly)
  - **Context** (Phone, Computer, Outdoors)

### Reminders
- Set morning reminders for daily tasks
- Use for time-sensitive items
- Keep reminder count reasonable (max 4 active)

## Backup Your Data

The database file is located at:
```
c:\Users\Hp\IdeaProjects\demo\ListLY\listly.db
```

To backup:
1. Close ListLY
2. Copy `listly.db` to safe location
3. To restore: Replace `listly.db` with backup copy

## Troubleshooting

### App Won't Start
```bash
# Check Java version
java --version

# Should be 21 or higher
# If not, install Java 21+

# Try clean build
mvn clean install
mvn javafx:run
```

### Database Errors
```bash
# Delete and recreate database
# WARNING: This deletes all data!
rm listly.db
mvn javafx:run
```

### FXML Load Errors
- Ensure all FXML files are in `src/main/resources/org/example/listly/`
- Check controller class names match in FXML `fx:controller` attribute

## Getting Help

If you encounter issues:
1. Check [README.md](README.md) for detailed documentation
2. Review error messages in console
3. Verify all dependencies are installed
4. Try clean rebuild: `mvn clean install`

## Next Steps

Now that you're set up:
- ✅ Create your first category and list
- ✅ Add some tasks
- ✅ Try Focus Mode for a work session
- ✅ Set a reminder
- ✅ Customize colors and themes
- ✅ Explore different list types
- ✅ View duration tracking

Happy organizing with ListLY! 🎯
