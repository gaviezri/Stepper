package body.library;

import body.library.definition.DefinitionController;
import body.library.input.InputController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;


public class LibraryController extends body.BodyControllerComponent{

    @FXML private InputController inputComponentController;
    @FXML private DefinitionController definitionComponentController;
    @FXML public AnchorPane libraryAnchorPane;
    @FXML private AnchorPane inputComponent;
    @FXML private AnchorPane definitionComponent;

    public void initialize(){
        inputComponentController.setLibraryController(this);
        definitionComponentController.setLibraryController(this);
        libraryAnchorPane.getChildren().get(0).setVisible(true);
        libraryAnchorPane.getChildren().get(1).setVisible(false);
        inputComponentController.initializeBackButton(inputComponent, definitionComponent);
    }
    public void bindInputPaneEnablementToSelectButton() {
        definitionComponentController.bindInputPaneEnablementToSelectButton(inputComponent, definitionComponent);
    }

    public void bindLibraryTabComponents() {
        definitionComponentController.bindDefinitionTabComponents();
    }

    public InputController getInputComponentController() {
        return this.inputComponentController;
    }

    public DefinitionController getDefinitionController() {
        return this.definitionComponentController;
    }

    public void setInputSectionToContinuation(String flowNameContinuedTo,List<Pair<String,String>> continuationDataMap) {
        definitionComponentController.setContinuationFlowInputs(flowNameContinuedTo, continuationDataMap);

    }

    public void showInputComponent() {
        libraryAnchorPane.getChildren().get(0).setVisible(false);
        libraryAnchorPane.getChildren().get(1).setVisible(true);
    }
}
