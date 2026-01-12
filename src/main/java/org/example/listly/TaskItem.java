package org.example.listly;

import java.util.UUID;

public class TaskItem {
    public String id;
    public String listId;
    public String name;
    public boolean checked;
    public long startTime;
    public long endTime;

    public int textColor;
    public String fontStyle;
    public int position;

    public TaskItem() {
        this.id = UUID.randomUUID().toString();
        this.textColor = 0xFFFFFFFF;
        this.fontStyle = "NORMAL";
        this.position = 0;
    }

    public TaskItem(String name, boolean checked, long startTime, long endTime) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.checked = checked;
        this.startTime = startTime;
        this.endTime = endTime;
        this.textColor = 0xFFFFFFFF;
        this.fontStyle = "NORMAL";
        this.position = 0;
    }

    public TaskItem(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.checked = false;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.textColor = 0xFFFFFFFF;
        this.fontStyle = "NORMAL";
        this.position = 0;
    }
}
