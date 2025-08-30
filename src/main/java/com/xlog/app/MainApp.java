
package com.xlog.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/xlog/app/dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 1280, 800);
            scene.getStylesheets().add(getClass().getResource("/com/xlog/app/style.css").toExternalForm());
            stage.setTitle("Zenith");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){ launch(args); }
}
