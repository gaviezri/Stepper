import app.AppController;
import body.BodyController;
import header.HeaderController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import stepper.controller.EngineController;
import stepper.statistics.StatisticsManager;

public class App extends Application {
    private final String ICON_PATH = System.getProperty("user.dir") + "\\GUI\\resources\\steppericon.png";
    private EngineController engineController = EngineController.getInstance();
    private StatisticsManager statisticsManager = new StatisticsManager(engineController.getArchive());
    private AppController appController;
    @Override
    public void start(Stage primaryStage) throws Exception {
        utils.Utils.whoRunsMe(new Object(){}.getClass().getEnclosingMethod().getName());
        // get loaders
        FXMLLoader appLoader = new FXMLLoader(AppController.class.getResource("app.fxml"));
        ScrollPane root = appLoader.load();
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
