package login;

import communication.UserRequestsDispatcher;
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
    private Text forgotPasswordText;
    @FXML
    private Label userExistsLabel;
    @FXML
    private AnchorPane loginPane;
    private Stage loginStage;

    private boolean loggedIn = false;

    public void setStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    private Font defaultForgotPassFont;

    public void initialize() {
        defaultForgotPassFont = forgotPasswordText.getFont();
        userNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                loginButton.setDisable(false);
            } else {
                loginButton.setDisable(true);
            }
        });
    }

    public void makeTextDarker(MouseEvent mouseEvent) {
        // make text bold
        forgotPasswordText.setFont(Font.font(defaultForgotPassFont.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, defaultForgotPassFont.getSize()));
    }

    public void makeTextBrighter(MouseEvent mouseEvent) {
        // make text normal
        forgotPasswordText.setFont(Font.font(defaultForgotPassFont.getFamily(), FontWeight.NORMAL, FontPosture.REGULAR, defaultForgotPassFont.getSize()));
    }

    public void tryLogin(MouseEvent mouseEvent) {
        boolean result = UserRequestsDispatcher.getInstance().login(userNameTextField.getText());
        if (result) {
            loggedIn = true;
            userExistsLabel.setVisible(false);
            loginStage.close();

        } else {
            userExistsLabel.setVisible(true);
        }
    }
}
