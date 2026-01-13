package org.example.listly;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeboxController {

    @FXML
    private AnchorPane root;

    @FXML
    private Label tvCategory;

    @FXML
    private ListView<RowItem> rvRows;

    private Stage mainStage;

    public void setStage(Stage stage) {
        this.mainStage = stage;
    }

    public void setCategoryName(String name) {
        tvCategory.setText(name);
    }

    public void setRowItems(ObservableList<RowItem> rows) {
        rvRows.setItems(rows);
    }

    @FXML
    private void initialize() {
        rvRows.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(RowItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.lists.isEmpty()) {
                    setGraphic(null);
                    return;
                }
                try {
                    FXMLLoader loader =
                            new FXMLLoader(getClass().getResource("/item_list.fxml"));
                    Node node = loader.load();
                    ItemListController c = loader.getController();

                    ListItem meta = item.lists.get(0); // one list per row
                    c.bindRowItem(item);
                    c.setOpenListHandler(HomeboxController.this::openListScreen);

                    setGraphic(node);
                } catch (IOException e) {
                    e.printStackTrace();
                    setGraphic(null);
                }
            }
        });
    }
    private void openListScreen(ListItem meta) {
        if (mainStage == null) {
            mainStage = (Stage) root.getScene().getWindow();
        }
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/checkboxscreen.fxml"));

            // FIX: cast to Parent
            javafx.scene.Parent rootNode = loader.load();

            CheckboxscreenController controller = loader.getController();
            controller.configureFromMeta(meta);

            Scene scene = new Scene(rootNode);
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
