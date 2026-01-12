package org.example.listly;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:listly.db";
    private static Database instance;

    private Database() {
        try (Connection c = getConnection();
                Statement st = c.createStatement()) {

            st.execute("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id TEXT PRIMARY KEY,
                        name TEXT,
                        created_at INTEGER,
                        updated_at INTEGER
                    )
                    """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS lists (
                        id TEXT PRIMARY KEY,
                        category_id TEXT,
                        title TEXT,
                        style TEXT,
                        theme_image TEXT,
                        text_color INTEGER,
                        font_size REAL,
                        font_style TEXT,
                        created_at INTEGER,
                        updated_at INTEGER,
                        total_duration_ms INTEGER
                    )
                    """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS tasks (
                        id TEXT PRIMARY KEY,
                        list_id TEXT,
                        name TEXT,
                        checked INTEGER,
                        start_time INTEGER,
                        end_time INTEGER,
                        text_color INTEGER,
                        font_style TEXT,
                        position INTEGER DEFAULT 0
                    )
                    """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS reminders (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        task_name TEXT NOT NULL,
                        hour INTEGER,
                        minute INTEGER,
                        enabled INTEGER DEFAULT 0,
                        reminder_key TEXT UNIQUE
                    )
                    """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS focus_sessions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        mode TEXT,
                        duration_planned INTEGER,
                        duration_done INTEGER,
                        date_ms INTEGER
                    )
                    """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS preferences (
                        key TEXT PRIMARY KEY,
                        value TEXT
                    )
                    """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Database get() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
