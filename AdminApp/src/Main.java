import communication.AdminRequestsDispatcher;
import GUI.app.AppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
    private AppController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (AdminRequestsDispatcher.getInstance().isAdminOnline()) {
            GUI.utils.Utils.ShowError("Error",
                    "Application is already running",
                    "Only single instance is allowed.");
            return;
        }
        FXMLLoader appLoader = new FXMLLoader(AppController.class.getResource("app.fxml"));
        ScrollPane root = appLoader.load();
        mainController = appLoader.getController();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stepper");

         Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
             mainController.doFinalize();
         });

        primaryStage.setOnCloseRequest(event -> {
                    mainController.doFinalize();
        });
        primaryStage.show();
    }
}