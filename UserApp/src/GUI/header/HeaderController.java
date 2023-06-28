package GUI.header;

import GUI.app.AppController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Objects;


public class HeaderController {
    private AppController mainController;
    @FXML
    private Text userNameText;
    @FXML
    private Text isManagerText;
    @FXML
    private ToggleButton rolesButton;
    @FXML
    private ComboBox<String> rolesCombo;

    public void initialize() {
        initializeRolesButton();
        rolesCombo.setMouseTransparent(true);
        rolesCombo.setFocusTraversable(false);
    }

    private void initializeRolesButton() {
        rolesCombo.setItems(FXCollections.observableArrayList("None"));

        rolesButton.setOnAction(event -> {
            if (rolesButton.isSelected()) {
                rolesCombo.show();
            } else {
                rolesCombo.hide();
            }
        });

        rolesButton.setOnMouseExited(event -> {
            rolesButton.setSelected(false);
        });
    }
    public String getUserName() {
        return userNameText.getText();
    }

    public void setMainController(AppController appController) {
        this.mainController = appController;
    }

    public void bindUserNameToText(TextField userNameTextField) {
        this.userNameText.textProperty().bind(userNameTextField.textProperty());
    }

    public void updateManagerAndRoleText(String isManager) {
        if (!isManagerText.getText().equals(isManager)) {
            isManagerText.setText(isManager);
        }

        List roles = mainController.getUserRolesList();
        if (roles.isEmpty()) {
            rolesCombo.setItems(FXCollections.observableArrayList("None"));
        } else {
            rolesCombo.setItems(FXCollections.observableArrayList(roles));
        }
    }
}
