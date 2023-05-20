package body;

import app.AppController;
import body.definition.FlowDefinitionController;
import javafx.fxml.FXML;

public class BodyController {
    @FXML
    private FlowDefinitionController flowDefinitionController;

    public void setMainController(AppController appController) {
        appController.setBodyController(this);
    }
}
