package body.library.input;

import body.library.LibraryControllerComponent;
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
import java.util.stream.Collectors;

public class InputController extends LibraryControllerComponent {
    // NESTED CLASS INPUTFIELD
    // holds all the data needed to create an input field, and the input field itself
    public static class InputField {
        private static Map<Node,InputField> Node2InputField = new HashMap<>();
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

        public static void clearInputFields() {
            Node2InputField.clear();
        }
        public static InputField getInputFieldOfElement(Node node){
            return  Node2InputField.get(node);
        }
        public String getName() {
            return name;
        }
        public String getType() {
            return type;
        }

        public String getContent(){
            switch(type){
                case "String":
                case "List":
                    return ((TextField) inputFieldElement).getText();
                case "Integer":
                    return ((TextField) inputFieldElement).getText();
                case "Double":
                    return ((TextField) inputFieldElement).getText();
                case "Enum":
                    return ((ComboBox) inputFieldElement).getValue().toString();
                case "Json":
                    return ((TextArea) inputFieldElement).getText();
            }
            return null;
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
            Node2InputField.put(inputFieldElement,this);
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

        // SET PLACEHOLDER TEXT
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
            // TODO: add HTTP Call Step options

            return null;
        }

        public Node getInputFieldElement() {
            return inputFieldElement;
        }

    }
    // END OF NESTED CLASS INPUTFIELD

    // Enum fields for the map of inputs data from DTO
    public enum INPUT_FIELDS{
        NAME,
        TYPE,
        USED_BY,
        USER_STRING
    }
    // END OF ENUM INPUT_FIELDS

    // Input Controller's fields
    @FXML Label inputsLabel;
    @FXML ScrollPane inputsScrollPane;
    @FXML VBox inputsVBox;
    @FXML Tooltip startFlowToolTip;
    @FXML Button startFlowButton;
    @FXML Button backToDefinitionButton;
    @FXML Pane buttonWrapperForToolTip;
    BooleanProperty allMandatorySatisfied = new SimpleBooleanProperty(false);

    // END OF Input Controller's fields

    public void initialize() {
        startFlowButton.setDisable(true);
        initializeButtonToolTip();
        initializeStartButton();
        inputsVBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        inputsVBox.setSpacing(15);
    }

    private void initializeButtonToolTip(){
        startFlowToolTip.setWrapText(true);
        startFlowToolTip.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 14));
        buttonWrapperForToolTip.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            if(allMandatorySatisfied.get()){
                startFlowToolTip.setText("Click to start the flow");
            }else{
                startFlowToolTip.setText("To start, fill the mandatory inputs.\n" +
                        "If you want, optional inputs can be provided but are optional ;-)");
            }
            startFlowToolTip.show(buttonWrapperForToolTip, event.getScreenX(), event.getScreenY());
        });
        buttonWrapperForToolTip.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            startFlowToolTip.hide();
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
    private void initializeStartButton(){
        ImageView iv = new ImageView(getClass().getResource("start-button.png").toString());
        iv.setFitHeight(160);
        iv.setFitWidth(160);
        iv.setPreserveRatio(true);
        startFlowButton.setGraphic(iv);
        startFlowButton.backgroundProperty().set(null);
        startFlowButton.setOnMousePressed(event -> {
            startFlowButton.translateYProperty().set(3);
        });
        startFlowButton.setOnMouseReleased(event -> {
            startFlowButton.translateYProperty().set(-3);
        });
        allMandatorySatisfied.addListener((observable, oldValue, newValue) -> {
            if(newValue){
                startFlowButton.setDisable(false);
            }else{
                startFlowButton.setDisable(true);
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

    public Button getStartButton() {
        return startFlowButton;
    }

    // Create the Pair of 2 Map<String,String> maps that will be used to initialize the context in flow execution
    public Pair<Map, Map> getValName2ValType() {
        Map<String, String> Name2Val = new HashMap<>();
        Map<String, String> Name2Type = new HashMap<>();

        inputsVBox.getChildren().forEach(x -> {
            if (x instanceof TitledPane) {
                TitledPane titledPane = (TitledPane) x;
                VBox internalVBox = (VBox) titledPane.getContent();
                internalVBox.getChildren().forEach(y -> {

                    System.out.println(y.getClass().getSimpleName());
                    InputField inputField = InputField.getInputFieldOfElement(y);
                    String content = inputField.getContent();
                    if(! content.equals("")) {
                        String inputName = inputField.getName();
                        String inputType = inputField.getType();
                        if (inputType.equals("Enum")){
                            inputType = "Enumeration";
                        }
                        Name2Val.put(inputName, content);
                        Name2Type.put(inputName, inputType);
                    }
                });
            }
        });
        return new Pair<>(Name2Val, Name2Type);
    }

}
