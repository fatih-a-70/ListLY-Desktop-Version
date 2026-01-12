package org.example.listly;

import java.util.ArrayList;
import java.util.List;

public class ListItem {
    public String id;
    public String categoryId;
    public String title;
    public String prefKey;
    public ListStyle style;
    public String themeImage;
    public int textColor;
    public float fontSizeSp;
    public String fontStyle;

    public long createdAt;
    public long updatedAt;

    public long totalDurationMs;

    public List<TaskItem> tasks = new ArrayList<>();
}
