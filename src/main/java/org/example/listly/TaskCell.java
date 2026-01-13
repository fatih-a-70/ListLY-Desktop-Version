package org.example.listly;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

import java.util.Arrays;

public class TaskCell extends ListCell<TaskItem> {

    private final ListStyle style;
    private final Runnable saveCallback;

    // UI for CHECKBOX lists
    private final CheckBox cb = new CheckBox();

    // UI for non‑checkbox lists
    private final Label textLabel = new Label();
    private final HBox textBox = new HBox(textLabel);   // simple container

    private long lastClick = 0L;

    private static final String STYLE_NORMAL = "NORMAL";
    private static final String STYLE_BOLD = "BOLD";
    private static final String STYLE_ITALIC = "ITALIC";

    public TaskCell(ListStyle style, Runnable saveCallback) {
        this.style = style;
        this.saveCallback = saveCallback;
    }

    @Override
    protected void updateItem(TaskItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        String fs = item.fontStyle != null ? item.fontStyle : STYLE_NORMAL;

        if (style == ListStyle.CHECKBOX) {
            // ---------- real checkbox list ----------
            cb.setOnAction(null);
            cb.setOnMouseClicked(null);

            StringBuilder sb = new StringBuilder("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");
            if (STYLE_BOLD.equals(fs)) sb.append("-fx-font-weight: bold;");
            if (STYLE_ITALIC.equals(fs)) sb.append("-fx-font-style: italic;");
            cb.setStyle(sb.toString());

            cb.setDisable(false);
            cb.setText(item.name);
            cb.setSelected(item.checked);

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
                if (e.getButton() != MouseButton.PRIMARY) return;
                long now = System.currentTimeMillis();
                if (now - lastClick < 300) {
                    showTaskOptions(item);
                }
                lastClick = now;
            });

            setGraphic(cb);

        } else {
            // ---------- SIMPLE / WISHLIST / NOTE / MEMO: text only ----------
            textLabel.setOnMouseClicked(null);

            StringBuilder sb = new StringBuilder("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");
            if (STYLE_BOLD.equals(fs)) sb.append("-fx-font-weight: bold;");
            if (STYLE_ITALIC.equals(fs)) sb.append("-fx-font-style: italic;");
            textLabel.setStyle(sb.toString());

            String prefix = "";
            if (style == ListStyle.PLAIN) {
                prefix = " • ";
            } else if (style == ListStyle.WISHLIST) {
                prefix = " ★ ";
            }
            // NOTE and MEMO keep prefix empty
            textLabel.setText(prefix + item.name);

            textLabel.setOnMouseClicked(e -> {
                if (e.getButton() != MouseButton.PRIMARY) return;
                long now = System.currentTimeMillis();
                if (now - lastClick < 300) {
                    editOrDelete(item);   // double‑click to edit/delete
                }
                lastClick = now;
            });

            setGraphic(textBox);
        }
    }

    private void showTaskOptions(TaskItem item) {
        String[] options = {
                "Edit Task", "Delete Task", "Task Duration",
                "Text Color", "Font Style"
        };
        int i = Dialogs.choice("Task Options", Arrays.asList(options));
        if (i == 0) editTask(item);
        else if (i == 1) deleteTask(item);
        else if (i == 2) showTaskDuration(item);
        else if (i == 3) pickTextColor(item);
        else if (i == 4) pickFontStyle(item);
    }

    private void editOrDelete(TaskItem item) {
        TextInputDialog d = new TextInputDialog(item.name);
        d.setTitle("Edit or Delete");
        d.setHeaderText(null);
        d.setContentText("Name:");
        d.showAndWait().ifPresent(s -> {
            String name = s.trim();
            if (name.isEmpty()) return;
            if (style == ListStyle.MEMO && name.split("\\s+").length > 100) {
                Dialogs.info("Too Long", "Memo must be 100 words or less.");
                return;
            }
            item.name = name;
            saveCallback.run();
        });
    }

    private void editTask(TaskItem item) {
        String name = Dialogs.input("Edit Task", "Name", item.name);
        if (name == null) return;
        name = name.trim();
        if (name.isEmpty()) return;
        item.name = name;
        saveCallback.run();
    }

    private void deleteTask(TaskItem item) {
        if (getListView() == null) return;
        getListView().getItems().remove(item);
        saveCallback.run();
    }

    private void showTaskDuration(TaskItem item) {
        long now = System.currentTimeMillis();
        long end = item.endTime != 0 ? item.endTime : now;
        long dur = end - item.startTime;
        if (dur < 0) dur = 0;
        String msg = "Duration: " + (dur / 1000) + " seconds";
        Dialogs.info("Task Duration", msg);
    }

    private void pickTextColor(TaskItem item) {
        String[] options = {"White", "Red", "Blue", "Black"};
        int[] colors = {0xFFFFFFFF, 0xFFFF0000, 0xFF0000FF, 0xFF000000};
        int i = Dialogs.choice("Select Text Color", Arrays.asList(options));
        if (i < 0) return;
        item.textColor = colors[i];
        saveCallback.run();
    }

    private void pickFontStyle(TaskItem item) {
        String[] styles = {"Normal", "Bold", "Italic"};
        int i = Dialogs.choice("Font Style", Arrays.asList(styles));
        if (i < 0) return;
        if (i == 1) item.fontStyle = STYLE_BOLD;
        else if (i == 2) item.fontStyle = STYLE_ITALIC;
        else item.fontStyle = STYLE_NORMAL;
        saveCallback.run();
    }
}
