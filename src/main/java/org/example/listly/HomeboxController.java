package org.example.listly;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

public class HomeboxController {
    @FXML AnchorPane root;
    @FXML Label tvCategory;
    @FXML ListView<RowItem> rvRows;
}
