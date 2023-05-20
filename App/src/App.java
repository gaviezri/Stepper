import app.AppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import stepper.controller.EngineController;
import stepper.statistics.StatisticsManager;

import java.net.URL;

public class App extends Application {

    private EngineController engineController = new EngineController();
    private StatisticsManager statisticsManager = new StatisticsManager(engineController.getArchive());
    @Override
    public void start(Stage primaryStage) throws Exception {
        //bind the engineController to the GUIManager
        //bind the statisticsManager to the GUIManager
        //bind the GUIManager to the primaryStage
        //start the GUIManager

        FXMLLoader loader = new FXMLLoader();
        URL url = AppController.class.getResource("app.fxml");
        loader.setLocation(url);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
