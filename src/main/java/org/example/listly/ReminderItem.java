package org.example.listly;

public class ReminderItem {
    public int id;
    public String taskName;
    public int hour;
    public int minute;
    public boolean enabled;
    public String reminderKey;

    public ReminderItem() {
        this.taskName = "Task";
        this.hour = 9;
        this.minute = 0;
        this.enabled = false;
    }

    public ReminderItem(String taskName, int hour, int minute) {
        this.taskName = taskName;
        this.hour = hour;
        this.minute = minute;
        this.enabled = false;
    }

    public String getTimeString() {
        return String.format("%02d:%02d", hour, minute);
    }
}
