package header;

import app.AppController;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import stepper.controller.EngineController;
import stepper.dto.flow.LoadDataDTO;



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
    private Menu systemMenu;

    @FXML
    private MenuItem saveSnapshot;

    @FXML
    private MenuItem loadSnapshot;

    @FXML
    private Menu themeMenu;

    @FXML
    private Menu changeThemeMenu;

    @FXML
    private MenuItem themeItem1;

    @FXML
    private MenuItem themeItem2;

    @FXML
    void BrowseBtnMouseEnter(MouseEvent event) {

    }

    @FXML
    void BrowseBtnMouseLeave(MouseEvent event) {

    }

    @FXML
    void BrowseBtnMousePress(MouseEvent event) {
        EngineController  engine = mainController.getEngineController();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Flow XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        java.io.File chosenFile = fileChooser.showOpenDialog(this.mainController.getPrimaryStage());
        if (chosenFile != null) {
            String path = chosenFile.getAbsolutePath();
            LoadDataDTO ldDTO =  engine.readXML(path);
            if(ldDTO.getStatus()){
                pathTextField.setText(path);
//              mainController.getBodyController().getFlowDefinitionController().setFlowDefinitionData(engine.getFlowDefinitionsNames());
                // some more logic...
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error while loading flow(s) from XML file");
                alert.setContentText(ldDTO.getMessage());
                alert.showAndWait();
            }
        }


    }

    public void setMainController(AppController appController) {
        this.mainController = appController;
    }

}
