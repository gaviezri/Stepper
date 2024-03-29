package GUI.login;

import communication.UserRequestsDispatcher;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class LoginController {
    @FXML
    private Button loginButton;
    @FXML
    private TextField userNameTextField;
    @FXML
    private Label userExistsLabel;
    @FXML
    private AnchorPane loginPane;
    private Stage loginStage;
    private Stage mainStage;

    private final BooleanProperty loggedIn = new SimpleBooleanProperty(false);

    public void setStage(Stage loginStage) {
        this.loginStage = loginStage;
    }
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public boolean isLoggedIn() {
        return loggedIn.get();
    }

    public BooleanProperty loggedInProperty() {
        return loggedIn;
    }



    public void initialize() {
        userNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.length() <= 0);
        });
    }


    public void tryLogin(MouseEvent mouseEvent) {
        boolean result = UserRequestsDispatcher.getInstance().login(userNameTextField.getText());
        if (result) {
            loggedIn.setValue(true);
            userExistsLabel.setVisible(false);
            loginStage.close();
            mainStage.show();

        } else {
            userExistsLabel.setVisible(true);
        }
    }

    public TextField getUserNameTextField() {
        return userNameTextField;
    }
}
