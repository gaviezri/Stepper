package body.library.input;

import body.library.LibraryControllerComponent;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import stepper.dto.flow.FlowDefinitionDTO;
import javafx.scene.control.TextFormatter.Change;
import java.util.function.UnaryOperator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class InputController extends LibraryControllerComponent {

    public class InputField {
        private String name;
        private String type;
        private String userString;
        private BooleanProperty satisfied;
        private Node InputFieldElement;

        public InputField(String name, String type, List<String> stepNames, String userString) {
            this.name = name;
            this.type = type;
            satisfied = new SimpleBooleanProperty(false);
        }
        private void createElement()
        {
            switch(type){
                case "String":
                    InputFieldElement = new TextField();
                    ((TextField)InputFieldElement).setPromptText(userString);
                    break;
                case "Number":
                case "Double":
                    Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]*");
                    UnaryOperator<Change> filter = change -> {
                        String newText = change.getControlNewText();
                        if (pattern.matcher(newText).matches()) {
                            return change;
                        }
                        return null;
                    };
                    TextFormatter<String> textFormatter = new TextFormatter<>(filter);
                    InputFieldElement = new TextField();
                    ((TextField)InputFieldElement).setTextFormatter(textFormatter);
                    ((TextField)InputFieldElement).setPromptText(userString);
                    break;
                case "Enum":
                    InputFieldElement = new ComboBox();

                    break;
                case "Json":
                    InputFieldElement = new TextArea();
                    // or
                    break;
            }
        }
        public Node getInputFieldElement() {
            return InputFieldElement;
        }

    }
    public enum INPUT_FIELDS{
        NAME,
        TYPE,
        USED_BY,
        USER_STRING

    }
    @FXML Label inputsLabel;
    @FXML ScrollPane inputsScrollPane;
    @FXML VBox inputsVBox;
    @FXML Button executeFlowButton;
    @FXML Tooltip executeFlowToolTip;
    @FXML Button backToDefinitionButton;
    @FXML Pane buttonWrapperForToolTip;
    @FXML Accordion inputsAccordion;

    BooleanProperty allMandatorySatisfied = new SimpleBooleanProperty(false);

    public void initialize() {
        executeFlowButton.setDisable(true);
        initializeButtonToolTip();
        initializeExecuteButton();
    }

    private void initializeButtonToolTip(){
        executeFlowToolTip.setWrapText(true);
        executeFlowToolTip.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 14));
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

    }

    public void setInputsToSelectedFlow(FlowDefinitionDTO dto) {
        allMandatorySatisfied.set(false);
        System.out.println("Setting inputs to selected flow");
        inputsVBox.getChildren().clear();
        inputsLabel.setText("Inputs for " + dto.getFlowName());
        Pair<Map<INPUT_FIELDS,List>,Map<INPUT_FIELDS,List>> MandatoryAndOptionalFieldMaps =
                createMandatoryAndOptionalFieldMaps(dto);
        Map<INPUT_FIELDS, List> mandatoryFields = MandatoryAndOptionalFieldMaps.getKey();
        Map<INPUT_FIELDS, List> optionalFields = MandatoryAndOptionalFieldMaps.getValue();


        if (mandatoryFields.get(INPUT_FIELDS.NAME).size() != 0) {
            TitledPane mandatoryInputsTitledPane = new TitledPane("Mandatory Inputs", new VBox());
            inputsAccordion.getPanes().add(mandatoryInputsTitledPane);

            for (int i = 0; i < mandatoryFields.get(INPUT_FIELDS.NAME).size(); i++) {
                String mandatoryInput = (String) mandatoryFields.get(INPUT_FIELDS.NAME).get(i);
                String mandatoryInputType = (String) mandatoryFields.get(INPUT_FIELDS.TYPE).get(i);
                String userString = (String) mandatoryFields.get(INPUT_FIELDS.USER_STRING).get(i);
                List<String> stepNames = mandatoryFields.get(INPUT_FIELDS.USED_BY);

                InputField mandatoryInputElement = new InputField(mandatoryInput, mandatoryInputType, stepNames, userString);
//                mandatoryInputElement.setMandatorySatisfiedProperty(allMandatorySatisfied);
                inputsVBox.getChildren().add(mandatoryInputElement.getInputFieldElement());
            }
        }
    }

    private static  Pair<Map<INPUT_FIELDS,List>,Map<INPUT_FIELDS,List>> createMandatoryAndOptionalFieldMaps(FlowDefinitionDTO dto) {
        List<Boolean> mandatoryInputsMask = dto.getFreeInputNecessity().stream()
                .map(x -> x.equals("MANDATORY"))
                .collect(java.util.stream.Collectors.toList());
        List<String> freeInputTypes = dto.getFreeInputTypes();
        List<String> freeInputsFinalNames = dto.getFreeInputsFinalNames();
        List<String> freeInputUserStrings = dto.getFreeInputUserString();

        List<Pair<String,List<String>>> freeInputStepNames = dto.getFreeInputs2StepsThatUseThem();

        List<String> mandatoryInputs = freeInputsFinalNames.stream()
                .filter(x -> mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                .collect(java.util.stream.Collectors.toList());

        List<String> optionalInputs = freeInputsFinalNames.stream()
                .filter(x -> !mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                .collect(java.util.stream.Collectors.toList());

        List<String> mandatoryInputsTypes = freeInputsFinalNames.stream()
                .filter(x -> mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                .map(x -> freeInputTypes.get(freeInputsFinalNames.indexOf(x)))
                .collect(java.util.stream.Collectors.toList());

        List<String> optionalInputsTypes = freeInputsFinalNames.stream()
                .filter(x -> !mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                .map(x -> freeInputTypes.get(freeInputsFinalNames.indexOf(x)))
                .collect(java.util.stream.Collectors.toList());

        List<String> mandatoryInputsUserString = freeInputUserStrings.stream()
                .filter(x -> mandatoryInputsMask.get(freeInputUserStrings.indexOf(x)))
                .collect(java.util.stream.Collectors.toList());

        List<String> optionalInputsUserString = freeInputUserStrings.stream()
                .filter(x -> !mandatoryInputsMask.get(freeInputUserStrings.indexOf(x)))
                .collect(java.util.stream.Collectors.toList());

        List<Pair<String,List<String>>> mandatoryInputsStepNames = freeInputStepNames.stream()
                .filter(x -> mandatoryInputsMask.get(freeInputStepNames.indexOf(x)))
                .collect(java.util.stream.Collectors.toList());

        List<Pair<String,List<String>>> optionalInputsStepNames = freeInputStepNames.stream()
                .filter(x -> !mandatoryInputsMask.get(freeInputStepNames.indexOf(x)))
                .collect(java.util.stream.Collectors.toList());



        Map<INPUT_FIELDS, List> mandatoryInputsMap = new HashMap<>();
        mandatoryInputsMap.put(INPUT_FIELDS.NAME, mandatoryInputs);
        mandatoryInputsMap.put(INPUT_FIELDS.TYPE, mandatoryInputsTypes);
        mandatoryInputsMap.put(INPUT_FIELDS.USER_STRING, mandatoryInputsUserString);
        mandatoryInputsMap.put(INPUT_FIELDS.USED_BY, mandatoryInputsStepNames);

        Map<INPUT_FIELDS, List> optionalInputsMap = new HashMap<>();
        optionalInputsMap.put(INPUT_FIELDS.NAME, optionalInputs);
        optionalInputsMap.put(INPUT_FIELDS.TYPE, optionalInputsTypes);
        optionalInputsMap.put(INPUT_FIELDS.USER_STRING, optionalInputsUserString);
        optionalInputsMap.put(INPUT_FIELDS.USED_BY, optionalInputsStepNames);

        return new Pair<>(mandatoryInputsMap, optionalInputsMap);
    }
}

