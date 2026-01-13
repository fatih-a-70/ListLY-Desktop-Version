package org.example.listly;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;

public class DeadlineController {

    @FXML
    private ListView<String> recycler;

    @FXML
    private ImageView imagebutton6;

    private final ObservableList<String> items = FXCollections.observableArrayList();

    // list metadata from HomeboxController
    private String listId;
    private String title;

    // called from HomeboxController
    public void init(String listId, String title) {
        this.listId = listId;
        this.title = title;
        // later you can use listId/title to load/save memo text
        // for now it keeps the simple add behavior
    }

    @FXML
    private void initialize() {
        recycler.setItems(items);
        imagebutton6.setOnMouseClicked(e -> items.add("Memo " + (items.size() + 1)));
    }
}
