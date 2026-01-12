package org.example.listly;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextInputDialog;

public class TaskCell extends ListCell<TaskItem> {

    private final ListStyle style;
    private final Runnable saveCallback;
    private final CheckBox cb = new CheckBox();
    private long lastClick = 0;

    public TaskCell(ListStyle style, Runnable saveCallback) {
        this.style = style;
        this.saveCallback = saveCallback;
        setGraphic(cb);
    }

    @Override
    protected void updateItem(TaskItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            return;
        }
        setGraphic(cb);
        cb.setOnAction(null);
        cb.setSelected(false);

        switch (style) {
            case CHECKBOX:
                cb.setText(item.name);
                cb.setSelected(item.checked);
                cb.setStyle("-fx-text-fill: white;");
                if ("BOLD".equals(item.fontStyle)) cb.setStyle(cb.getStyle() + "-fx-font-weight: bold;");
                if ("ITALIC".equals(item.fontStyle)) cb.setStyle(cb.getStyle() + "-fx-font-style: italic;");
                cb.setOnAction(e -> {
                    long now = System.currentTimeMillis();
                    boolean isChecked = cb.isSelected();
                    item.checked = isChecked;
                    if (isChecked) {
                        if (item.startTime == 0L) item.startTime = now;
                        item.endTime = now;
                    } else {
                        item.endTime = now;
                    }
                    saveCallback.run();
                });
                cb.setOnMouseClicked(e -> {
                    long now = System.currentTimeMillis();
                    if (now - lastClick < 300) showTaskOptions(item);
                    lastClick = now;
                });
                break;
            case WISHLIST:
                cb.setText("✨ " + item.name);
                cb.setOnMouseClicked(e -> editOrDelete(item));
                break;
            case PLAIN:
                cb.setText("• " + item.name);
                cb.setOnMouseClicked(e -> editOrDelete(item));
                break;
            case NOTE:
            case MEMO:
                cb.setText(item.name);
                cb.setOnMouseClicked(e -> editOrDelete(item));
                break;
        }
    }

    private void showTaskOptions(TaskItem item) {
        String[] options = {"Edit Task", "Delete Task", "Task Duration", "Text Color", "Font Style"};
        int i = Dialogs.choice("Task Options", java.util.Arrays.asList(options));
        if (i == 0) editTask(item);
        if (i == 1) deleteTask(item);
        if (i == 2) showTaskDuration(item);
        if (i == 3) pickTextColor(item);
        if (i == 4) pickFontStyle(item);
    }

    private void editOrDelete(TaskItem item) {
        TextInputDialog d = new TextInputDialog(item.name);
        d.setTitle("Edit or Delete");
        d.setHeaderText(null);
        d.setContentText("Name:");
        d.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CANCEL);
        d.showAndWait().ifPresent(s -> {
            item.name = s;
            saveCallback.run();
        });
    }

    private void editTask(TaskItem item) {
        String name = Dialogs.input("Edit Task", "Name", item.name);
        if (name == null) return;
        item.name = name.trim();
        saveCallback.run();
    }

    private void deleteTask(TaskItem item) {
        if (getListView() == null) return;
        getListView().getItems().remove(item);
        saveCallback.run();
    }

    private void showTaskDuration(TaskItem item) {
        long now = System.currentTimeMillis();
        long end = item.endTime > 0 ? item.endTime : now;
        long dur = end - item.startTime;
        if (dur < 0) dur = 0;
        String msg = "Duration: " + (dur / 1000) + " seconds";
        Dialogs.info("Task Duration", msg);
    }

    private void pickTextColor(TaskItem item) {
        String[] options = {"White", "Red", "Blue", "Black"};
        int[] colors = {
                0xFFFFFFFF,
                0xFFFF0000,
                0xFF0000FF,
                0xFF000000
        };
        int i = Dialogs.choice("Select Text Color", java.util.Arrays.asList(options));
        if (i < 0) return;
        item.textColor = colors[i];
        saveCallback.run();
    }

    private void pickFontStyle(TaskItem item) {
        String[] styles = {"Normal", "Bold", "Italic"};
        int i = Dialogs.choice("Font Style", java.util.Arrays.asList(styles));
        if (i < 0) return;
        if (i == 1) item.fontStyle = "BOLD";
        else if (i == 2) item.fontStyle = "ITALIC";
        else item.fontStyle = "NORMAL";
        saveCallback.run();
    }
}
