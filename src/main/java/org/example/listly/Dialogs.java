package org.example.listly;

import javafx.scene.control.*;

import java.util.List;

public class Dialogs {

    public static int choice(String title, List<String> options) {
        ChoiceDialog<String> d = new ChoiceDialog<>(options.get(0), options);
        d.setTitle(title);
        d.setHeaderText(null);
        d.setContentText(null);
        var r = d.showAndWait();
        if (r.isEmpty()) return -1;
        return options.indexOf(r.get());
    }

    public static String input(String title, String header, String initial) {
        TextInputDialog d = new TextInputDialog(initial);
        d.setTitle(title);
        d.setHeaderText(header);
        var r = d.showAndWait();
        return r.orElse(null);
    }

    public static boolean confirm(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        a.setTitle(title);
        a.setHeaderText(null);
        var r = a.showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }

    public static void info(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public static void addTaskDialog(List<TaskItem> tasks, Runnable save) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("New Task");
        d.setHeaderText("Task name");
        var r = d.showAndWait();
        if (r.isEmpty()) return;
        String name = r.get().trim();
        if (name.isEmpty()) return;
        TaskItem t = new TaskItem(name);
        tasks.add(t);
        save.run();
    }
}
