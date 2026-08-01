package com.relieftrack.app;

import com.relieftrack.config.AppConfig;
import com.relieftrack.database.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseInitializer.initializeDatabase();

        FXMLLoader loader = new FXMLLoader(Main.class.getResource(AppConfig.LOGIN_FXML));
        Scene scene = new Scene(loader.load(), AppConfig.WINDOW_WIDTH, AppConfig.WINDOW_HEIGHT);

        stage.setTitle(AppConfig.APP_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}