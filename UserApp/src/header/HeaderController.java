package header;

import app.AppController;
import dto.flow.FlowDefinitionDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import stepper.controller.EngineController;
import dto.flow.LoadDataDTO;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

import static utils.Utils.*;


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
        pathTextField.textProperty().bind(loadedXMLPath);
    }

    public void setMainController(AppController appController) {
        this.mainController = appController;
    }

    public void updateUserRole(List<FlowDefinitionDTO> allFlowDefinitionsData) {
    }
}
