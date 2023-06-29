package GUI.header;

import GUI.app.AppController;
import javafx.application.Platform;
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
    private ListView<String> rolesListView;

    public void initialize() {
        initializeRolesButton();
    }

    private void initializeRolesButton() {
        rolesListView.setDisable(true);
        rolesListView.setItems(FXCollections.observableArrayList("None"));
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
        Platform.runLater(()->{
            if (roles.isEmpty()) {
                rolesListView.setItems(FXCollections.observableArrayList("None"));
            } else {
                rolesListView.setItems(FXCollections.observableArrayList(roles));
            }
        });
    }
}
