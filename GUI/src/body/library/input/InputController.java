package body.library.input;

import body.library.LibraryControllerComponent;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import stepper.dto.flow.FlowDefinitionDTO;

import java.util.List;

public class InputController extends LibraryControllerComponent {
    @FXML Label inputsLabel;
    @FXML ScrollPane inputsScrollPane;
    @FXML VBox inputsVBox;
    @FXML Button executeFlowButton;
    @FXML Tooltip executeFlowToolTip;
    @FXML Button backToDefinitionButton;
    @FXML Pane buttonWrapperForToolTip;

    BooleanProperty allMandatorySatisfied = new SimpleBooleanProperty(false);

    public void initialize() {
        executeFlowButton.setDisable(true);
        initializeButtonToolTip();
        initializeExecuteButton();
    }

    private void initializeButtonToolTip(){
        executeFlowToolTip.setWrapText(true);
        executeFlowToolTip.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 14));
        executeFlowToolTip.setShowDelay(javafx.util.Duration.millis(100));
        executeFlowToolTip.setHideDelay(javafx.util.Duration.millis(100));
    }

    public void initializeBackButton(AnchorPane inputPane, AnchorPane definitionPane){
        ImageView iv = new ImageView(getClass().getResource("back-button.png").toString());
        iv.setFitHeight(160);
        iv.setFitWidth(160);
        iv.setPreserveRatio(true);
        backToDefinitionButton.setGraphic(iv);
        backToDefinitionButton.backgroundProperty().set(null);
        backToDefinitionButton.setOnMousePressed(event -> {
            backToDefinitionButton.translateYProperty().set(3);
        });
        backToDefinitionButton.setOnMouseReleased(event -> {
            backToDefinitionButton.translateYProperty().set(-3);
        });
        backToDefinitionButton.setOnMouseClicked(event -> {
            inputPane.setVisible(false);
            definitionPane.setVisible(true);
        });
    }
    private void initializeExecuteButton(){
        ImageView iv = new ImageView(getClass().getResource("start-button.png").toString());
        iv.setFitHeight(160);
        iv.setFitWidth(160);
        iv.setPreserveRatio(true);
        executeFlowButton.setGraphic(iv);
        executeFlowButton.backgroundProperty().set(null);
        executeFlowButton.setOnMousePressed(event -> {
            executeFlowButton.translateYProperty().set(3);
        });
        executeFlowButton.setOnMouseReleased(event -> {
            executeFlowButton.translateYProperty().set(-3);
        });
        allMandatorySatisfied.addListener((observable, oldValue, newValue) -> {
            if(newValue){
                executeFlowButton.setDisable(false);
            }else{
                executeFlowButton.setDisable(true);
            }
        });
        buttonWrapperForToolTip.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            if(allMandatorySatisfied.get()){
                executeFlowToolTip.setText("Click to start the flow");
            }else{
                executeFlowToolTip.setText("To start, fill the mandatory inputs.\n" +
                        "If you want, optional inputs can be provided but are optional ;-)");
            }
            executeFlowToolTip.show(executeFlowButton, event.getScreenX(), event.getScreenY());
        });
        buttonWrapperForToolTip.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            executeFlowToolTip.hide();
        });
    }

    public void setInputsToSelectedFlow(FlowDefinitionDTO dto) {
        allMandatorySatisfied.set(false);
        System.out.println("Setting inputs to selected flow");
    }
}

