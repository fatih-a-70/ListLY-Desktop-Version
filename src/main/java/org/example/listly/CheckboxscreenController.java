package org.example.listly;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for checkboxscreen.fxml
 * Manages a list of tasks with different display styles and CRUD operations
 */
public class CheckboxscreenController {

    @FXML
    private AnchorPane root;
    @FXML
    private ListView<TaskItem> recycler;
    @FXML
    private Button imagebutton6;

    private final List<TaskItem> tasks = new ArrayList<>();
    private final TaskDao taskDao = new TaskDao();
    private final CategoryDao categoryDao = new CategoryDao();

    private ListItem currentList;
    private ListStyle style = ListStyle.CHECKBOX;

    /**
     * Initialize the controller with a ListItem
     * 
     * @param list The list item containing tasks to display
     */
    public void setListItem(ListItem list) {
        this.currentList = list;
        this.style = list.style != null ? list.style : ListStyle.CHECKBOX;
        loadTasks();
        applyTheme();
    }

    @FXML
    private void initialize() {
        // Setup cell factory for custom task rendering
        recycler.setCellFactory(listView -> createTaskCell());

        // Setup add button
        if (imagebutton6 != null) {
            imagebutton6.setOnAction(e -> addNewTask());
        }
    }

    /**
     * Create custom cell for rendering tasks based on list style
     */
    private ListCell<TaskItem> createTaskCell() {
        return new ListCell<TaskItem>() {
            private final CheckBox checkBox = new CheckBox();
            private final Label label = new Label();
            private long lastClickTime = 0;

            @Override
            protected void updateItem(TaskItem task, boolean empty) {
                super.updateItem(task, empty);

                if (empty || task == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                // Apply task styling
                applyTaskStyle(task);

                // Setup interaction based on style
                switch (style) {
                    case CHECKBOX:
                        renderCheckboxStyle(task);
                        break;
                    case WISHLIST:
                        renderWishlistStyle(task);
                        break;
                    case PLAIN:
                        renderPlainStyle(task);
                        break;
                    case NOTE:
                    case MEMO:
                        renderNoteStyle(task);
                        break;
                }

                // Double-click to show options
                setOnMouseClicked(e -> {
                    long now = System.currentTimeMillis();
                    if (now - lastClickTime < 300) {
                        showTaskOptions(task);
                    }
                    lastClickTime = now;
                });
            }

            private void renderCheckboxStyle(TaskItem task) {
                checkBox.setText(task.name);
                checkBox.setSelected(task.checked);
                checkBox.setOnAction(e -> toggleTaskCheck(task, checkBox.isSelected()));
                setGraphic(checkBox);
                setText(null);
            }

            private void renderWishlistStyle(TaskItem task) {
                label.setText("✨ " + task.name);
                setGraphic(label);
                setText(null);
            }

            private void renderPlainStyle(TaskItem task) {
                label.setText("• " + task.name);
                setGraphic(label);
                setText(null);
            }

            private void renderNoteStyle(TaskItem task) {
                label.setText(task.name);
                setGraphic(label);
                setText(null);
            }

            private void applyTaskStyle(TaskItem task) {
                // Apply text color
                Color color = intToColor(task.textColor);
                String colorStyle = String.format("-fx-text-fill: #%02X%02X%02X;",
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255));

                // Apply font style
                String fontStyle = "";
                if ("BOLD".equals(task.fontStyle)) {
                    fontStyle = "-fx-font-weight: bold;";
                } else if ("ITALIC".equals(task.fontStyle)) {
                    fontStyle = "-fx-font-style: italic;";
                }

                checkBox.setStyle(colorStyle + fontStyle);
                label.setStyle(colorStyle + fontStyle);
            }
        };
    }

    /**
     * Toggle task checked state and update times
     */
    private void toggleTaskCheck(TaskItem task, boolean isChecked) {
        long now = System.currentTimeMillis();
        task.checked = isChecked;

        if (isChecked) {
            if (task.startTime == 0L) {
                task.startTime = now;
            }
            task.endTime = now;
        } else {
            task.endTime = now;
        }

        saveTask(task);
        updateListDuration();
    }

    /**
     * Show options dialog for a task
     */
    private void showTaskOptions(TaskItem task) {
        List<String> options = Arrays.asList(
                "Edit Task",
                "Delete Task",
                "Task Duration",
                "Text Color",
                "Font Style");

        int choice = Dialogs.choice("Task Options", options);

        switch (choice) {
            case 0:
                editTask(task);
                break;
            case 1:
                deleteTask(task);
                break;
            case 2:
                showTaskDuration(task);
                break;
            case 3:
                changeTextColor(task);
                break;
            case 4:
                changeFontStyle(task);
                break;
        }
    }

    /**
     * Add a new task to the list
     */
    private void addNewTask() {
        String taskName = Dialogs.input("New Task", "Enter task name:", "");
        if (taskName == null || taskName.trim().isEmpty()) {
            return;
        }

        TaskItem newTask = new TaskItem(taskName.trim());
        newTask.listId = currentList.id;
        newTask.position = tasks.size();
        newTask.startTime = System.currentTimeMillis();

        tasks.add(newTask);
        saveTask(newTask);
        recycler.getItems().add(newTask);
        updateListDuration();
    }

    /**
     * Edit an existing task
     */
    private void editTask(TaskItem task) {
        String newName = Dialogs.input("Edit Task", "Task name:", task.name);
        if (newName == null || newName.trim().isEmpty()) {
            return;
        }

        task.name = newName.trim();
        saveTask(task);
        recycler.refresh();
    }

    /**
     * Delete a task from the list
     */
    private void deleteTask(TaskItem task) {
        boolean confirmed = Dialogs.confirm("Delete Task",
                "Are you sure you want to delete \"" + task.name + "\"?");

        if (!confirmed) {
            return;
        }

        tasks.remove(task);
        taskDao.deleteTask(task.id);
        recycler.getItems().remove(task);

        // Update positions of remaining tasks
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).position = i;
            saveTask(tasks.get(i));
        }

        updateListDuration();
    }

    /**
     * Show task duration information
     */
    private void showTaskDuration(TaskItem task) {
        long now = System.currentTimeMillis();
        long end = task.endTime > 0 ? task.endTime : now;
        long duration = end - task.startTime;

        if (duration < 0) {
            duration = 0;
        }

        String durationStr = Dates.formatDuration(duration);
        String startStr = Dates.formatDateTime(task.startTime);
        String endStr = task.endTime > 0 ? Dates.formatDateTime(task.endTime) : "In Progress";

        String message = String.format(
                "Task: %s\n\nDuration: %s\nStart: %s\nEnd: %s",
                task.name, durationStr, startStr, endStr);

        Dialogs.info("Task Duration", message);
    }

    /**
     * Change task text color
     */
    private void changeTextColor(TaskItem task) {
        List<String> colorOptions = Arrays.asList(
                "White",
                "Black",
                "Red",
                "Green",
                "Blue",
                "Yellow",
                "Orange",
                "Purple");

        int[] colorValues = {
                0xFFFFFFFF, // White
                0xFF000000, // Black
                0xFFFF0000, // Red
                0xFF00FF00, // Green
                0xFF0000FF, // Blue
                0xFFFFFF00, // Yellow
                0xFFFFA500, // Orange
                0xFF800080 // Purple
        };

        int choice = Dialogs.choice("Select Text Color", colorOptions);
        if (choice >= 0 && choice < colorValues.length) {
            task.textColor = colorValues[choice];
            saveTask(task);
            recycler.refresh();
        }
    }

    /**
     * Change task font style
     */
    private void changeFontStyle(TaskItem task) {
        List<String> styleOptions = Arrays.asList("Normal", "Bold", "Italic");
        int choice = Dialogs.choice("Select Font Style", styleOptions);

        switch (choice) {
            case 0:
                task.fontStyle = "NORMAL";
                break;
            case 1:
                task.fontStyle = "BOLD";
                break;
            case 2:
                task.fontStyle = "ITALIC";
                break;
            default:
                return;
        }

        saveTask(task);
        recycler.refresh();
    }

    /**
     * Load tasks from database
     */
    private void loadTasks() {
        if (currentList == null || currentList.id == null) {
            return;
        }

        tasks.clear();
        tasks.addAll(taskDao.getTasksForList(currentList.id));
        recycler.getItems().setAll(tasks);
    }

    /**
     * Save a single task to database
     */
    private void saveTask(TaskItem task) {
        if (currentList != null && task.listId == null) {
            task.listId = currentList.id;
        }
        taskDao.saveTask(task);
    }

    /**
     * Save all tasks to database
     */
    private void saveAllTasks() {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).position = i;
            saveTask(tasks.get(i));
        }
    }

    /**
     * Update the total duration for the current list in database
     */
    private void updateListDuration() {
        if (currentList == null || currentList.id == null) {
            return;
        }

        long totalDuration = taskDao.calculateListDuration(currentList.id);
        categoryDao.updateListDuration(currentList.id, totalDuration);
        currentList.totalDurationMs = totalDuration;
    }

    /**
     * Apply theme/background to the view
     */
    private void applyTheme() {
        if (root == null) {
            System.err.println("Root is null in applyTheme");
            return;
        }

        try {
            String imagePath = "/images/" + (currentList != null && currentList.themeImage != null
                    ? currentList.themeImage
                    : "p8.jpg");
            System.out.println("Applying theme image: " + imagePath);

            var imageUrl = getClass().getResource(imagePath);
            if (imageUrl == null) {
                System.err.println("Theme image not found: " + imagePath);
                root.setStyle("-fx-background-color: #2C3E50;");
                return;
            }

            Image img = new Image(imageUrl.toExternalForm(), false);
            if (img.isError()) {
                System.err.println("Error loading theme image: " + imagePath);
                root.setStyle("-fx-background-color: #2C3E50;");
                return;
            }

            BackgroundSize size = new BackgroundSize(
                    BackgroundSize.AUTO, BackgroundSize.AUTO,
                    false, false, true, true);
            BackgroundImage bg = new BackgroundImage(img,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    size);
            root.setBackground(new Background(bg));
            System.out.println("Successfully applied theme: " + imagePath);
        } catch (Exception e) {
            System.err.println("Exception applying theme: " + e.getMessage());
            e.printStackTrace();
            // If image not found, use default background
            root.setStyle("-fx-background-color: #2C3E50;");
        }
    }

    /**
     * Convert integer color to JavaFX Color
     */
    private Color intToColor(int color) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return Color.rgb(r, g, b, a / 255.0);
    }
}
