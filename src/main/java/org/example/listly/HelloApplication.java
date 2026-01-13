package org.example.listly;
import javafx.scene.input.KeyCode;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Database.get();

        ImageCache.preload(
                "/images/p1.jpg",
                "/images/p2.jpg",
                "/images/p3.jpg",
                "/images/p4.jpg",
                "/images/p5.jpg",
                "/images/p6.jpg",
                "/images/p0.jpg",
                "/images/p7.jpg",
                "/images/p8.jpg",
                "/images/p9.jpg",
                "/images/g0.jpg",
                "/images/g9.jpg"
        );

        System.out.println("BG -> " +
                HelloApplication.class.getResource("/images/p6.jpg"));

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("activity_main.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 700);

        MainController controller = loader.getController();

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.BACK_SPACE) {
                controller.goHome();
                e.consume();
            }
        });

        // ⇧⇧⇧

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
