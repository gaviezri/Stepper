import app.AppController;
import body.BodyController;
import header.HeaderController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
        utils.Utils.whoRunsMe(new Object(){}.getClass().getEnclosingMethod().getName());
        FXMLLoader Apploader = new FXMLLoader(AppController.class.getResource("app.fxml"));
        FXMLLoader HeaderLoader = new FXMLLoader(HeaderController.class.getResource("header.fxml"));
        FXMLLoader BodyLoader = new FXMLLoader(BodyController.class.getResource("body.fxml"));


        ScrollPane root = Apploader.load();
        appController = Apploader.getController();
        GridPane header = HeaderLoader.load();
        HeaderController headerController = HeaderLoader.getController();
        TabPane body = BodyLoader.load();
        BodyController bodyController = BodyLoader.getController();

        BorderPane.class.cast(root.getContent()).setTop(header);
        BorderPane.class.cast(root.getContent()).setCenter(body);

        appController.setBodyController(bodyController);
        appController.setHeaderController(headerController);



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
