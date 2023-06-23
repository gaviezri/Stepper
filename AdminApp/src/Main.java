import Communication.StepperRequestsDispatcher;
import GUI.app.AppController;
import GUI.utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.util.Map;

public class Main extends Application {
    private AppController mainController;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader appLoader = new FXMLLoader(AppController.class.getResource("app.fxml"));
        ScrollPane root = appLoader.load();
        mainController = appLoader.getController();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stepper");
        // primaryStage.setOnCloseRequest(event -> {
        //     close all threads}
        primaryStage.show();
    }
}