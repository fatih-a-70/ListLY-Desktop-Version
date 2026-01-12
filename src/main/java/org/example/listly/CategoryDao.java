package org.example.listly;

import java.sql.*;
import java.util.*;

public class CategoryDao {

    public List<CategoryItem> getAllCategories() {
        List<CategoryItem> categories = new ArrayList<>();
        try (Connection conn = Database.get().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM categories ORDER BY created_at DESC")) {

            while (rs.next()) {
                CategoryItem cat = new CategoryItem();
                cat.id = rs.getString("id");
                cat.name = rs.getString("name");
                cat.createdAt = rs.getLong("created_at");
                cat.updatedAt = rs.getLong("updated_at");
                cat.lists = getListsForCategory(cat.id);
                cat.recalcDuration();
                categories.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    private List<ListItem> getListsForCategory(String categoryId) {
        List<ListItem> lists = new ArrayList<>();
        try (Connection conn = Database.get().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT * FROM lists WHERE category_id = ? ORDER BY created_at")) {

            pstmt.setString(1, categoryId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ListItem item = new ListItem();
                item.id = rs.getString("id");
                item.categoryId = categoryId;
                item.title = rs.getString("title");
                item.style = ListStyle.valueOf(rs.getString("style"));
                item.themeImage = rs.getString("theme_image");
                item.textColor = rs.getInt("text_color");
                item.fontSizeSp = rs.getFloat("font_size");
                item.fontStyle = rs.getString("font_style");
                item.createdAt = rs.getLong("created_at");
                item.updatedAt = rs.getLong("updated_at");
                item.totalDurationMs = rs.getLong("total_duration_ms");
                lists.add(item);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }

    public void saveCategory(CategoryItem category) {
        try (Connection conn = Database.get().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT OR REPLACE INTO categories (id, name, created_at, updated_at) VALUES (?, ?, ?, ?)")) {

            pstmt.setString(1, category.id);
            pstmt.setString(2, category.name);
            pstmt.setLong(3, category.createdAt);
            pstmt.setLong(4, category.updatedAt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveList(ListItem list) {
        try (Connection conn = Database.get().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT OR REPLACE INTO lists (id, category_id, title, style, theme_image, " +
                                "text_color, font_size, font_style, created_at, updated_at, total_duration_ms) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            pstmt.setString(1, list.id);
            pstmt.setString(2, list.categoryId);
            pstmt.setString(3, list.title);
            pstmt.setString(4, list.style.name());
            pstmt.setString(5, list.themeImage);
            pstmt.setInt(6, list.textColor);
            pstmt.setFloat(7, list.fontSizeSp);
            pstmt.setString(8, list.fontStyle);
            pstmt.setLong(9, list.createdAt);
            pstmt.setLong(10, list.updatedAt);
            pstmt.setLong(11, list.totalDurationMs);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCategory(String categoryId) {
        try (Connection conn = Database.get().getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM categories WHERE id = ?")) {

            pstmt.setString(1, categoryId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteList(String listId) {
        try (Connection conn = Database.get().getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM lists WHERE id = ?")) {

            pstmt.setString(1, listId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateListDuration(String listId, long totalMs) {
        try (Connection conn = Database.get().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE lists SET total_duration_ms = ?, updated_at = ? WHERE id = ?")) {

            pstmt.setLong(1, totalMs);
            pstmt.setLong(2, System.currentTimeMillis());
            pstmt.setString(3, listId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Legacy support methods
    public static List<CategoryItem> loadAll() {
        return new CategoryDao().getAllCategories();
    }

    public static void saveAll(List<CategoryItem> list) {
        CategoryDao dao = new CategoryDao();
        for (CategoryItem cat : list) {
            dao.saveCategory(cat);
            if (cat.lists != null) {
                for (ListItem li : cat.lists) {
                    dao.saveList(li);
                }
            }
        }
    }
}
