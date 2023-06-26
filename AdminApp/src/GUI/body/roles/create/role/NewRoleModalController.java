package GUI.body.roles.create.role;

import communication.Role;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class NewRoleModalController {

    @FXML private TextField roleNameTextField;

    @FXML private TextField roleDescripTextField;

    @FXML private Button createRoleButton;

    @FXML private AnchorPane rootAnchorPane;

    private Stage stage;

    private Role newRole = null;


    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> {
            newRole = null;
        });
    }

    @FXML public void initialize() {
        createRoleButton.disableProperty().bind(Bindings.or(roleNameTextField.textProperty().isEmpty(), roleDescripTextField.textProperty().isEmpty()));
        createRoleButton.setOnAction(event -> {
            newRole = new Role(roleNameTextField.getText(), roleDescripTextField.getText());
            stage.close();
        });
    }

    public Role getNewRole() {
       return newRole;
    }
}
