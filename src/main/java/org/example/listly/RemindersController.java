package org.example.listly;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

public class RemindersController {

    @FXML private Label task1;
    @FXML private Label task2;
    @FXML private Label task3;
    @FXML private Label task4;

    @FXML private Label time1;
    @FXML private Label time2;
    @FXML private Label time3;
    @FXML private Label time4;

    @FXML private ToggleButton sw1;
    @FXML private ToggleButton sw2;
    @FXML private ToggleButton sw3;
    @FXML private ToggleButton sw4;

    private static class ReminderData {
        String taskName = "";
        String timeText = "";
        boolean enabled = false;
        int hour = -1;
        int minute = -1;
    }

    private final ReminderData r1 = new ReminderData();
    private final ReminderData r2 = new ReminderData();
    private final ReminderData r3 = new ReminderData();
    private final ReminderData r4 = new ReminderData();

    @FXML
    private void initialize() {
        loadReminder(r1, task1, time1, "r1");
        loadReminder(r2, task2, time2, "r2");
        loadReminder(r3, task3, time3, "r3");
        loadReminder(r4, task4, time4, "r4");

        sw1.setSelected(r1.enabled);
        sw2.setSelected(r2.enabled);
        sw3.setSelected(r3.enabled);
        sw4.setSelected(r4.enabled);

        task1.setOnMouseClicked(v -> editTaskName(task1, r1, "r1"));
        task2.setOnMouseClicked(v -> editTaskName(task2, r2, "r2"));
        task3.setOnMouseClicked(v -> editTaskName(task3, r3, "r3"));
        task4.setOnMouseClicked(v -> editTaskName(task4, r4, "r4"));

        time1.setOnMouseClicked(v -> editTime(r1, time1, "r1"));
        time2.setOnMouseClicked(v -> editTime(r2, time2, "r2"));
        time3.setOnMouseClicked(v -> editTime(r3, time3, "r3"));
        time4.setOnMouseClicked(v -> editTime(r4, time4, "r4"));

        sw1.selectedProperty().addListener((b, o, n) -> toggleReminder(r1, n, "r1"));
        sw2.selectedProperty().addListener((b, o, n) -> toggleReminder(r2, n, "r2"));
        sw3.selectedProperty().addListener((b, o, n) -> toggleReminder(r3, n, "r3"));
        sw4.selectedProperty().addListener((b, o, n) -> toggleReminder(r4, n, "r4"));
    }

    private void loadReminder(ReminderData data, Label taskView, Label timeView, String key) {
        data.taskName = Prefs.get(key + "_task", taskView.getText());
        data.hour = Prefs.getInt(key + "_hour", -1);
        data.minute = Prefs.getInt(key + "_minute", -1);
        data.enabled = Prefs.getBool(key + "_enabled", false);
        if (data.hour >= 0 && data.minute >= 0) {
            data.timeText = String.format("%02d:%02d", data.hour, data.minute);
            timeView.setText(data.timeText);
        }
        taskView.setText(data.taskName);
    }

    private void saveReminder(ReminderData data, String key) {
        Prefs.put(key + "_task", data.taskName);
        Prefs.putInt(key + "_hour", data.hour);
        Prefs.putInt(key + "_minute", data.minute);
        Prefs.putBool(key + "_enabled", data.enabled);
    }

    private void editTaskName(Label tv, ReminderData data, String key) {
        String t = Dialogs.input("Edit task name", "Name", tv.getText());
        if (t == null || t.trim().isEmpty()) return;
        tv.setText(t);
        data.taskName = t;
        saveReminder(data, key);
    }

    private void editTime(ReminderData data, Label timeView, String key) {
        String t = Dialogs.input("Edit time", "HH:MM (24h)", "");
        if (t == null || t.isEmpty() || !t.contains(":")) return;
        String[] parts = t.split(":");
        try {
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            data.hour = h;
            data.minute = m;
            data.timeText = String.format("%02d:%02d", h, m);
            timeView.setText(data.timeText);
            saveReminder(data, key);
        } catch (Exception ignored) {
        }
    }

    private void toggleReminder(ReminderData data, boolean enable, String key) {
        data.enabled = enable;
        saveReminder(data, key);
        if (!enable) {
            Dialogs.info("Reminder Off", "Reminder disabled for " + data.taskName);
            return;
        }
        if (data.hour < 0 || data.minute < 0 || data.taskName.isEmpty()) {
            Dialogs.info("Error", "Set task name and time first");
            return;
        }
        String msg = "Reminder: " + data.taskName + " at " + data.timeText;
        Dialogs.info("Reminder On", msg);
    }
}
