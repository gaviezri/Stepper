
import GUI.app.AppController;
import communication.UserRequestsDispatcher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import GUI.login.LoginController;

import java.io.IOException;

public class Main extends Application {
    private AppController mainController;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(AppController.class.getResource("app.fxml"));
        ScrollPane root = loader.load();
        mainController = loader.getController();

        startLoginModal(primaryStage);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stepper");
        primaryStage.setOnCloseRequest(event -> {
            UserRequestsDispatcher.getInstance().logout();
            mainController.stop();
        });
        Thread.setDefaultUncaughtExceptionHandler((thread,throwable) -> {
            mainController.doFinalize();
        });

    }

    private void startLoginModal(Stage primaryStage) throws IOException {
        FXMLLoader loginLoader = new FXMLLoader(LoginController.class.getResource("login.fxml"));
        AnchorPane root = loginLoader.load();
        Stage loginStage = new Stage();
        LoginController loginController = loginLoader.getController();
        loginStage.setScene(new Scene(root));
        loginStage.setTitle("Stepper - Login");
        loginController.setStage(loginStage);
        loginController.setMainStage(primaryStage);
        mainController.bindUserNameToText(loginController.getUserNameTextField());
        loginStage.show();

        loginStage.setOnCloseRequest(event -> {
            // Handle the close request of the login stage
            if (!loginController.isLoggedIn()) {
                // The user is not logged in, so quit the application
                Platform.exit();
                this.mainController.stop();
            }
        });
    }

}