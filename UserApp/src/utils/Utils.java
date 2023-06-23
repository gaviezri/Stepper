package utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.text.Text;

public class Utils {

    private static void showAlert(Alert.AlertType alertType, String title, String header, String content) {

        Platform.runLater(()-> {
            Alert alert = new Alert(alertType);
            Text text = new Text(content);
            if (alertType == Alert.AlertType.ERROR){
                alert.getDialogPane().setGraphic(new javafx.scene.image.ImageView(new Image("file:///" + System.getProperty("user.dir") + "/GUI/resources/missings.jpg")));
            }

            text.wrappingWidthProperty().bind(alert.getDialogPane().widthProperty());
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });

    }
    public static void ShowInformation(String title, String header, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, header, content);
    }

    public static void ShowWarning(String title, String header, String content) {
        showAlert(Alert.AlertType.WARNING, title, header, content);
    }

    public static void ShowError(String title, String header, String content) {
        showAlert(Alert.AlertType.ERROR, title, header, content);
    }
    public static void whoRunsMe(String funcName) {
        System.out.println(funcName + " running on thread " + Thread.currentThread().getName());
    }

}
