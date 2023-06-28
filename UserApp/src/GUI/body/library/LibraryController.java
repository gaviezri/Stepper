package GUI.body.library;

import GUI.body.BodyControllerComponent;
import GUI.body.library.definition.DefinitionController;
import GUI.body.library.input.InputController;
import dto.flow.FlowDefinitionDTO;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;


public class LibraryController extends BodyControllerComponent {

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

    public AnchorPane getInputComponent() {
        return inputComponent;
    }

    public AnchorPane getDefinitionComponent() {
        return definitionComponent;
    }

    public void bindInputPaneEnablementToSelectButton() {
        definitionComponentController.bindInputPaneEnablementToSelectButton(inputComponent, definitionComponent);
    }

    public void updateFlowDefinitions(List<FlowDefinitionDTO> flowDefinitionDTOList) {
        definitionComponentController.updateFlowDefinitions(flowDefinitionDTOList);
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

    public FlowDefinitionDTO getFlowDefinitionsDataByIndex(int flowIndex) {
        return definitionComponentController.getFlowDefinitionsDataByIndex(flowIndex);
    }

    public void setInputsToSelectedFlow(FlowDefinitionDTO flowDefinitionDTO, Map<String,Object> continuationValues, boolean isRerun) {
        inputComponentController.setInputsToSelectedFlow(flowDefinitionDTO, continuationValues, isRerun);
    }
}
