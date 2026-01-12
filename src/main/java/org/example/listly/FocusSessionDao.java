package org.example.listly;

import java.sql.*;
import java.util.*;

public class FocusSessionDao {

    public List<FocusSession> getAllSessions() {
        List<FocusSession> sessions = new ArrayList<>();
        try (Connection conn = Database.get().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM focus_sessions ORDER BY date_ms DESC")) {

            while (rs.next()) {
                FocusSession session = new FocusSession();
                session.id = rs.getInt("id");
                session.mode = rs.getString("mode");
                session.durationPlanned = rs.getLong("duration_planned");
                session.durationDone = rs.getLong("duration_done");
                session.dateMs = rs.getLong("date_ms");
                sessions.add(session);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public void saveSession(FocusSession session) {
        try (Connection conn = Database.get().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO focus_sessions (mode, duration_planned, duration_done, date_ms) VALUES (?, ?, ?, ?)")) {

            pstmt.setString(1, session.mode);
            pstmt.setLong(2, session.durationPlanned);
            pstmt.setLong(3, session.durationDone);
            pstmt.setLong(4, session.dateMs);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSession(int id) {
        try (Connection conn = Database.get().getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM focus_sessions WHERE id = ?")) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearAll() {
        try (Connection conn = Database.get().getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM focus_sessions");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
