package org.example.listly;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;

public class TravelController {

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
        // later you can use listId/title to load/save notes
        // for now it just keeps your old behavior (Trip 1, Trip 2...)
    }

    @FXML
    private void initialize() {
        recycler.setItems(items);
        imagebutton6.setOnMouseClicked(e -> items.add("Trip " + (items.size() + 1)));
    }
}


