package GUI.header;

import GUI.app.AppController;
import dto.flow.FlowDefinitionDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import dto.flow.LoadDataDTO;

import java.util.List;


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
    private Text rolesAssignedText;

    private final StringProperty loadedXMLPath = new SimpleStringProperty();


    public void initialize() {
    }

    public void setMainController(AppController appController) {
        this.mainController = appController;
    }

    public void updateUserRole(List<FlowDefinitionDTO> allFlowDefinitionsData) {
    }

    public void bindUserNameToText(TextField userNameTextField) {
        this.userNameText.textProperty().bind(userNameTextField.textProperty());
    }
}
