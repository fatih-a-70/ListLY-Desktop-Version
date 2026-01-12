package org.example.listly;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;

public class ShoppingController {

    @FXML
    private ListView<String> recycler;

    @FXML
    private ImageView imagebutton6;

    private final ObservableList<String> items =
            FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        recycler.setItems(items);
        imagebutton6.setOnMouseClicked(e -> {
            items.add("Item " + (items.size() + 1));
        });
    }
}
