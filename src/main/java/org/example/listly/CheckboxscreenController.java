package org.example.listly;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.List;

public class CheckboxscreenController {

    @FXML
    private AnchorPane root;

    @FXML
    private ListView<TaskItem> recycler;

    @FXML
    private ImageView imagebutton6;

    private final ObservableList<TaskItem> tasks = FXCollections.observableArrayList();

    private ListStyle style = ListStyle.CHECKBOX;
    private String prefKey;   // list id used as key
    private String listId;
    private String title;
    private String themeImage;

    private final TaskDao dao = new TaskDao();

    // called from MainController when user opens a list
    public void configureFromMeta(ListItem meta) {
        this.style = meta.style;
        this.prefKey = meta.id;
        this.listId = meta.id;
        this.title = meta.title;
        this.themeImage = meta.themeImage;

        applyBackgroundTheme();
        loadTasks();

        // IMPORTANT: cell factory with this list's style
        recycler.setCellFactory(list -> new TaskCell(style, this::saveTasks));
    }

    @FXML
    private void initialize() {
        recycler.setItems(tasks);
        // no style here; it will be set in configureFromMeta

        // add button icon
        URL iconUrl = getClass().getResource("/images/add2.jpg");
        if (iconUrl != null) {
            imagebutton6.setImage(new Image(iconUrl.toExternalForm()));
        }

        imagebutton6.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showAddDialog();
            }
        });
    }

    private void applyBackgroundTheme() {
        if (root == null) return;

        try {
            String file = (themeImage == null || themeImage.isEmpty())
                    ? "p6.jpg"
                    : themeImage;

            String path = "/images/" + file;
            URL url = getClass().getResource(path);
            System.out.println("List BG -> " + path + " url=" + url);

            if (url == null) {
                System.err.println("Theme image not found: " + path);
                root.setStyle("-fx-background-color: black;");
                return;
            }

            String css = String.format(
                    "-fx-background-image: url('%s');" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-position: center center;" +
                            "-fx-background-size: cover;",
                    url.toExternalForm()
            );
            root.setStyle(css);
        } catch (Exception ex) {
            ex.printStackTrace();
            root.setStyle("-fx-background-color: black;");
        }
    }

    private void loadTasks() {
        tasks.clear();
        if (prefKey != null) {
            List<TaskItem> loaded = TaskDao.loadTasks(prefKey);
            tasks.addAll(loaded);
        }
    }

    private void saveTasks() {
        if (prefKey == null) return;
        TaskDao.saveTasks(prefKey, tasks);
        if (listId != null) {
            long total = dao.calculateListDuration(listId);
            System.out.println("Total duration for list " + listId + " = " + total + " ms");
        }
    }

    private void showAddDialog() {
        javafx.scene.control.TextInputDialog dialog =
                new javafx.scene.control.TextInputDialog();
        dialog.setTitle("New Task");
        dialog.setHeaderText(null);
        dialog.setContentText("Task name:");
        dialog.showAndWait().ifPresent(name -> {
            String trimmed = name.trim();
            if (trimmed.isEmpty()) return;

            TaskItem t = new TaskItem(trimmed);
            t.listId = prefKey;
            t.position = tasks.size();
            tasks.add(t);
            saveTasks();
        });
    }
}
