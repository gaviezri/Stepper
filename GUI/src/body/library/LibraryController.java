package body.library;

import body.BodyController;
import body.library.definition.DefinitionController;
import body.library.input.InputController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;


public class LibraryController extends body.BodyControllerComponent{

    private BodyController bodyController;
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
}
