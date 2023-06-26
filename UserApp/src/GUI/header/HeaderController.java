package GUI.header;

import GUI.app.AppController;
import communication.UserRequestsDispatcher;
import dto.flow.FlowDefinitionDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import dto.flow.LoadDataDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class HeaderController {
    private AppController mainController;

    @FXML
    private GridPane topBanner;

    @FXML
    private FlowPane fileSelectionPane;

    @FXML
    private TextField pathTextField;

    @FXML
    private Button browseBtn;

    @FXML
    private Text userNameText;
    @FXML
    private Text isManagerText;
    @FXML
    private Button rolesButton;

    @FXML
    private ComboBox rolesCombo;

    private final StringProperty loadedXMLPath = new SimpleStringProperty();


    public void initialize() {
        initializeRolesButton();
    }
    private void initializeRolesButton(){
        rolesCombo.setItems(FXCollections.observableArrayList("None"));
        rolesButton.setOnAction(event -> {
            if(!rolesCombo.isShowing()) {
                rolesCombo.setValue(null);   // cancel the mark of the current (hidden behind the button) selected role
                rolesCombo.show();
            }
            else{
                rolesCombo.hide();
            }
        });
    }
    public String getUserName(){
        return userNameText.getText();
    }
    public void setMainController(AppController appController) {
        this.mainController = appController;
    }

    public void bindUserNameToText(TextField userNameTextField) {
        this.userNameText.textProperty().bind(userNameTextField.textProperty());
    }

    public void updateManagerAndRoleText(String isManager){
        if(!isManagerText.getText().equals(isManager)){
            isManagerText.setText(isManager);
        }

        List roles = UserRequestsDispatcher.getInstance().getUserRolesList();
        rolesCombo.setItems(FXCollections.observableArrayList(roles));
    }
}
