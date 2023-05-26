package body.library.input;

import body.library.LibraryControllerComponent;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
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
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import stepper.dto.flow.FlowDefinitionDTO;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class InputController extends LibraryControllerComponent {

    public Button getInputExecuteButton() {
        return executeFlowButton;
    }

    public Pair<Map, Map> getValName2ValType() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public class InputField {
        private String name;
        private String type;
        private String userString;
        private List<String> stepNames;
        private BooleanProperty satisfied;
        private Node inputFieldElement;


        public InputField(String name, String type, String userString, List<String> stepNames) {
            this.name = name;
            this.type = type;
            this.userString = userString;
            satisfied = new SimpleBooleanProperty(false);
            this.stepNames = stepNames;
        }
        private void createElement()
        {
            switch(type){
                case "String":
                case "List":
                    setStringInputField();
                    break;
                case "Integer":
                    setNumericTextField(createIntegerFormatter());
                    break;
                case "Double":
                    setNumericTextField(createDoubleFormatter());
                    break;
                case "Enum":
                    setEnumComboBox();
                    break;
                case "Json":
                    setJsonTextArea();
                    // or
                    break;
            }


        }

        public BooleanProperty getSatisfied() {
            return satisfied;
        }
        private void setJsonTextArea() {
            inputFieldElement = new TextArea();
            ((TextArea) inputFieldElement).setPromptText(setTextualPrompt("|"));
            satisfied.bind(((TextArea) inputFieldElement).textProperty().isNotEmpty());
        }

        private void setEnumComboBox() {
            inputFieldElement = new ComboBox();
            ((ComboBox) inputFieldElement).setPromptText(userString);
            ((ComboBox) inputFieldElement).getItems().addAll(createEnumOptions());
            satisfied.bind(((ComboBox) inputFieldElement).valueProperty().isNotNull());
        }

        private TextFormatter<Double> createDoubleFormatter() {
            return new TextFormatter<>(new DoubleStringConverter(), 0.0, change -> {
                String newText = change.getControlNewText();
                if (newText.matches("-?\\d*(\\.\\d*)?")) {
                    return change;
                }
                return null;
            });
        }

        private TextFormatter<Integer> createIntegerFormatter() {
            return new TextFormatter<>(new IntegerStringConverter(), 0, change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d*")) {
                    return change;
                }
                return null;
            });
        }
        private void setNumericTextField(TextFormatter filter) {
            inputFieldElement = new TextField();
            ((TextField) inputFieldElement).setTextFormatter(filter);
            ((TextField) inputFieldElement).setPromptText(setTextualPrompt(" |"));
            ((TextField) inputFieldElement).setText("");
            satisfied.bind(((TextField) inputFieldElement).textProperty().isNotEmpty());
        }

        private void setStringInputField() {
            inputFieldElement = new TextField();
            ((TextField) inputFieldElement).setPromptText(setTextualPrompt("|"));
            satisfied.bind(((TextField) inputFieldElement).textProperty().isNotEmpty());
        }

        private String setTextualPrompt(String replacement) {
            return userString + " Used By: " + stepNames.toString().replace(",", replacement);
        }

        private List<String> createEnumOptions() {
            if (userString == "Operation type") {
                List<String> zipOptions = new LinkedList<>();
                zipOptions.add("ZIP");
                zipOptions.add("UNZIP");
                return zipOptions;
            }
//                if (stepName.toLowerCase().contains())

            return null;
        }

        public Node getInputFieldElement() {
            return inputFieldElement;
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
    @FXML Tooltip executeFlowToolTip;
    @FXML Button executeFlowButton;
    @FXML Button backToDefinitionButton;
    @FXML Pane buttonWrapperForToolTip;

    BooleanProperty allMandatorySatisfied = new SimpleBooleanProperty(false);

    public void initialize() {
        executeFlowButton.setDisable(true);
        initializeButtonToolTip();
        initializeExecuteButton();
        inputsVBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        inputsVBox.setSpacing(15);
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
        System.out.println("Setting inputs to selected flow");
        inputsVBox.getChildren().clear();
        inputsLabel.setText("Inputs for " + dto.getFlowName());
        Pair<Map<INPUT_FIELDS,List>,Map<INPUT_FIELDS,List>> MandatoryAndOptionalFieldMaps =
                createMandatoryAndOptionalFieldMaps(dto);
        Map<INPUT_FIELDS, List> mandatoryFields = MandatoryAndOptionalFieldMaps.getKey();
        Map<INPUT_FIELDS, List> optionalFields = MandatoryAndOptionalFieldMaps.getValue();


        if (mandatoryFields.get(INPUT_FIELDS.NAME).size() != 0) {
            setInputFieldElements(mandatoryFields, true);
        }
        if (optionalFields.get(INPUT_FIELDS.NAME).size() != 0) {
            setInputFieldElements(optionalFields, false);
        }
    }

    private void setInputFieldElements(Map<INPUT_FIELDS, List> fields, Boolean mandatory) {
        VBox internalInputFieldVBox = new VBox();
        TitledPane InputsTitledPane = new TitledPane((mandatory ? "Mandatory Fields" : "Optional Fields"),internalInputFieldVBox);
        InputsTitledPane.setExpanded(false);
        inputsVBox.getChildren().add(InputsTitledPane);
        internalInputFieldVBox.setSpacing(15);
        List<InputField> allFields = new LinkedList<>();
        for (int i = 0; i < fields.get(INPUT_FIELDS.NAME).size(); i++) {
            String inputName = (String) fields.get(INPUT_FIELDS.NAME).get(i);
            String inputType = (String) fields.get(INPUT_FIELDS.TYPE).get(i);
            String userString = (String) fields.get(INPUT_FIELDS.USER_STRING).get(i);
            Pair<String,List<String>> stepNames =(Pair) fields.get(INPUT_FIELDS.USED_BY).get(i);

            InputField inputElement = new InputField(inputName, inputType, userString, stepNames.getValue());
            inputElement.createElement();
            allFields.add(inputElement);
            internalInputFieldVBox.getChildren().add(inputElement.getInputFieldElement());
        }
        if (mandatory){
            allMandatorySatisfied.bind(Bindings.createBooleanBinding(() ->
                    allFields.stream().allMatch(x -> x.satisfied.get()), allFields.stream().map(x -> x.satisfied).collect(Collectors.toList()).toArray(new BooleanProperty[0])));
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

        List<String> optionalInputsUserString = freeInputsFinalNames.stream()
                .filter(x -> !mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                .map(x -> freeInputUserStrings.get(freeInputsFinalNames.indexOf(x)))
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

