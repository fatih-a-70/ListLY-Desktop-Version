package org.example.listly;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class ItemListController {

    @FXML
    private AnchorPane root;

    @FXML
    private Label tvTitle;

    @FXML
    private Pane previewContainer;

    // one ListItem per row
    private ListItem meta;
    private long lastClick = 0L;

    // callback set by HomeboxController
    private java.util.function.Consumer<ListItem> openListHandler;

    public void setOpenListHandler(java.util.function.Consumer<ListItem> handler) {
        this.openListHandler = handler;
    }

    // RowItem holds a List<ListItem> in your code
    public void bindRowItem(RowItem rowItem) {
        // assuming exactly one list per row
        if (rowItem == null || rowItem.lists.isEmpty()) {
            meta = null;
            tvTitle.setText("");
            previewContainer.getChildren().clear();
            return;
        }

        this.meta = rowItem.lists.get(0);

        // Use whatever fields your desktop ListItem actually has.
        // From your Android code it's at least: id, title, style, prefKey.[file:3]
        tvTitle.setText(meta.title != null ? meta.title : "");

        // Simple default styling; you can extend ListItem later with textColor/font info.
        tvTitle.setStyle("-fx-text-fill: #030320; -fx-font-size: 16px;");

        // For now no background image because ListItem has no theme info yet.
        previewContainer.setStyle("-fx-background-color: rgba(0,0,0,0.15);");
        previewContainer.getChildren().clear();

        // Double-click on the whole item opens the list
        root.setOnMouseClicked(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            long now = System.currentTimeMillis();
            if (now - lastClick < 250) {   // double click
                if (openListHandler != null && meta != null) {
                    openListHandler.accept(meta);
                }
            }
            lastClick = now;
        });
    }
}
