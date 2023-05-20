import app.AppController;
import body.BodyController;
import header.HeaderController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import stepper.controller.EngineController;
import stepper.statistics.StatisticsManager;

import java.net.URL;
import java.nio.file.Path;

public class App extends Application {
    private final String ICON_PATH = System.getProperty("user.dir") + "\\GUI\\resources\\steppericon.png";
    private EngineController engineController = EngineController.getInstance();
    private StatisticsManager statisticsManager = new StatisticsManager(engineController.getArchive());
    private AppController appController;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader Apploader = new FXMLLoader(AppController.class.getResource("app.fxml"));
        FXMLLoader BodyLoader = new FXMLLoader(HeaderController.class.getResource("header.fxml"));
        FXMLLoader HeaderLoader = new FXMLLoader(BodyController.class.getResource("body.fxml"));

        Parent root = Apploader.load();
        appController = Apploader.getController();

        appController.setHeaderController(BodyLoader.getController());
        appController.setBodyController(HeaderLoader.getController());




        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stepper");

        primaryStage.getIcons().add(new javafx.scene.image.Image("file:///" + ICON_PATH));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
