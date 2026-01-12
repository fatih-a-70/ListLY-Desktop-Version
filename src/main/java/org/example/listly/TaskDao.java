package org.example.listly;

import java.sql.*;
import java.util.*;

public class TaskDao {

    public List<TaskItem> getTasksForList(String listId) {
        List<TaskItem> tasks = new ArrayList<>();
        try (Connection conn = Database.get().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT * FROM tasks WHERE list_id = ? ORDER BY position")) {

            pstmt.setString(1, listId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                TaskItem task = new TaskItem();
                task.id = rs.getString("id");
                task.listId = listId;
                task.name = rs.getString("name");
                task.checked = rs.getInt("checked") == 1;
                task.startTime = rs.getLong("start_time");
                task.endTime = rs.getLong("end_time");
                task.textColor = rs.getInt("text_color");
                task.fontStyle = rs.getString("font_style");
                task.position = rs.getInt("position");
                tasks.add(task);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void saveTask(TaskItem task) {
        try (Connection conn = Database.get().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT OR REPLACE INTO tasks (id, list_id, name, checked, start_time, " +
                                "end_time, text_color, font_style, position) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            pstmt.setString(1, task.id);
            pstmt.setString(2, task.listId);
            pstmt.setString(3, task.name);
            pstmt.setInt(4, task.checked ? 1 : 0);
            pstmt.setLong(5, task.startTime);
            pstmt.setLong(6, task.endTime);
            pstmt.setInt(7, task.textColor);
            pstmt.setString(8, task.fontStyle);
            pstmt.setInt(9, task.position);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTask(String taskId) {
        try (Connection conn = Database.get().getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tasks WHERE id = ?")) {

            pstmt.setString(1, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveAllTasks(List<TaskItem> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            TaskItem task = tasks.get(i);
            task.position = i;
            saveTask(task);
        }
    }

    public long calculateListDuration(String listId) {
        long total = 0;
        List<TaskItem> tasks = getTasksForList(listId);
        long now = System.currentTimeMillis();
        for (TaskItem task : tasks) {
            long end = task.endTime > 0 ? task.endTime : now;
            long dur = end - task.startTime;
            if (dur > 0)
                total += dur;
        }
        return total;
    }

    // Legacy support methods
    private static String fileForKey(String key) {
        return "tasks_" + key + ".dat";
    }

    public static List<TaskItem> loadTasks(String key) {
        if (key == null)
            return new ArrayList<>();
        // Try to find the list_id for this key
        try (Connection conn = Database.get().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT id FROM lists WHERE id = ? LIMIT 1")) {
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new TaskDao().getTasksForList(key);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void saveTasks(String key, List<TaskItem> tasks) {
        if (key == null)
            return;
        TaskDao dao = new TaskDao();
        for (int i = 0; i < tasks.size(); i++) {
            TaskItem task = tasks.get(i);
            task.listId = key;
            task.position = i;
            dao.saveTask(task);
        }
    }
}
