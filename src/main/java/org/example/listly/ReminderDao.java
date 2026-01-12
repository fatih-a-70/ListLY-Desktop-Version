package org.example.listly;

import java.sql.*;
import java.util.*;

public class ReminderDao {

    public List<ReminderItem> getAllReminders() {
        List<ReminderItem> reminders = new ArrayList<>();
        try (Connection conn = Database.get().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM reminders ORDER BY id")) {

            while (rs.next()) {
                ReminderItem reminder = new ReminderItem();
                reminder.id = rs.getInt("id");
                reminder.taskName = rs.getString("task_name");
                reminder.hour = rs.getInt("hour");
                reminder.minute = rs.getInt("minute");
                reminder.enabled = rs.getInt("enabled") == 1;
                reminder.reminderKey = rs.getString("reminder_key");
                reminders.add(reminder);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reminders;
    }

    public void saveReminder(ReminderItem reminder) {
        try (Connection conn = Database.get().getConnection()) {
            if (reminder.id == 0) {
                // Insert
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO reminders (task_name, hour, minute, enabled, reminder_key) VALUES (?, ?, ?, ?, ?)");
                pstmt.setString(1, reminder.taskName);
                pstmt.setInt(2, reminder.hour);
                pstmt.setInt(3, reminder.minute);
                pstmt.setInt(4, reminder.enabled ? 1 : 0);
                pstmt.setString(5, reminder.reminderKey);
                pstmt.executeUpdate();
            } else {
                // Update
                PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE reminders SET task_name=?, hour=?, minute=?, enabled=?, reminder_key=? WHERE id=?");
                pstmt.setString(1, reminder.taskName);
                pstmt.setInt(2, reminder.hour);
                pstmt.setInt(3, reminder.minute);
                pstmt.setInt(4, reminder.enabled ? 1 : 0);
                pstmt.setString(5, reminder.reminderKey);
                pstmt.setInt(6, reminder.id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteReminder(int id) {
        try (Connection conn = Database.get().getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM reminders WHERE id = ?")) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
