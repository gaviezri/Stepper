package body.definition;

import body.BodyController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import stepper.dto.flow.FlowNamesDTO;

public class DefinitionController {
    public static int counter = 0;

    public DefinitionController() {
        System.out.println("DefinitionController constructor called " + counter++);
    }
    BodyController bodyController;
    @FXML
    private GridPane flowDefGridPane;
    @FXML
    private ScrollPane flowDefAvailableFlowsScroll;
    @FXML
    private ListView<String> flowDefAvailableFlowsList;
    @FXML
    private Label selectedFlowLabel;

    public void showFlowNames(FlowNamesDTO flowDefinitionsNames) {

        flowDefAvailableFlowsList.getItems().clear();
        flowDefAvailableFlowsList.getItems().addAll(flowDefinitionsNames.getFlowNames());
        flowDefAvailableFlowsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleFlowSelection(newValue);
            }
        });
    }

    private void handleFlowSelection(Object newValue) {

    }

//    private static StackPane createNewFlowNameButton(String flowName) {
//        StackPane flowNamePane = new StackPane();
//        flowNamePane.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
//            // Show flow details and ask for inputs/continuation
//            System.out.println("Clicked on " + flowName);
//        });
//        Rectangle flowNameRectangle = new Rectangle(100, 50);
//        flowNameRectangle.setFill(Color.AQUA);
//        flowNamePane.getChildren().addAll(flowNameRectangle, new javafx.scene.control.Label(flowName));
//        return flowNamePane;
//    }

    public void setBodyController(BodyController bodyController) {
        this.bodyController = bodyController;
    }
}
