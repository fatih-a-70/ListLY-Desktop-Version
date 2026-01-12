package org.example.listly;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize database
        Database.get();

        System.out.println("BG -> " +
                HelloApplication.class.getResource("/images/p6.jpg"));

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("activity_main.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 700);

        stage.setScene(scene);
        stage.setTitle("ListLY Desktop");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
