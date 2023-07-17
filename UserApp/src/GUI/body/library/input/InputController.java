package GUI.body.library.input;

import GUI.body.execution.ExecutionController;
import GUI.body.library.LibraryControllerComponent;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import dto.flow.FlowDefinitionDTO;

import javax.xml.soap.Text;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
        private boolean isMandatory;
        private static List<InputField> allFields = new LinkedList<>();
        private Boolean isInitialValue = false;
        private Node inputFieldElement;


        public InputField(String name, String type, String userString, List<String> stepNames, boolean isMandatory) {
            this.name = name;
            this.type = type;
            this.userString = userString;
            this.stepNames = stepNames;
            this.isMandatory = isMandatory;
            satisfied = new SimpleBooleanProperty(false);
            allFields.add(this);
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
                    if (inputFieldElement instanceof TextField)
                        return ((TextField) inputFieldElement).getText();
                    else
                        return ((TextField)((HBox) inputFieldElement).getChildren().get(0)).getText();

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

        private void createElement(Map<String, Object> initialValues, Map<String,Object> continuationValues,Boolean isReRun) {
            switch (type) {
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
            Node2InputField.put(inputFieldElement, this);
            if (initialValues.containsKey(name)) {
                setPresetValuesInElement(initialValues, true);
            } else if (continuationValues != null && continuationValues.containsKey(name)) {
                setPresetValuesInElement(continuationValues, false, isReRun);
            }
        }

        private void setPresetValuesInElement(Map<String, Object> initialValues, boolean isInitialValue){
            setPresetValuesInElement(initialValues,isInitialValue,false);
        }

        private void setPresetValuesInElement(Map<String, Object> initialValues, boolean isInitialValue, Boolean isReRun) {
            this.satisfied.unbind();
            this.satisfied.setValue(true);
            this.isInitialValue = isInitialValue;
            if (type.equals("Enum")) {
                ((ComboBox) inputFieldElement).setValue(initialValues.get(name).toString());
            } else if (inputFieldElement instanceof TextField) {
                ((TextField) inputFieldElement).setText(initialValues.get(name).toString());
            } else {
                // HBox
                ((TextField) ((HBox) inputFieldElement).getChildren().get(0)).setText(initialValues.get(name).toString());
            }
            inputFieldElement.setDisable(isReRun);
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
            TextField theTextContent;
            if (name.toLowerCase().contains("folder") || name.toLowerCase().contains("file")){
                theTextContent = createResourceInputField();
            } else {
                theTextContent = createSimpleStringInputField();
            }
            satisfied.bind((theTextContent.textProperty().isNotEmpty()));

        }

        private TextField createSimpleStringInputField() {
            inputFieldElement = new TextField();
            ((TextField) inputFieldElement).setPromptText(setTextualPrompt(" |"));
            return (TextField) inputFieldElement;
        }

        private TextField createResourceInputField() {
            inputFieldElement = new HBox();
            HBox wrapper = (HBox) inputFieldElement;
            wrapper.setAlignment(Pos.CENTER_LEFT);
            wrapper.setSpacing(10);
            TextField textField = new TextField();
            textField.setPromptText(setTextualPrompt(" |"));
            wrapper.getChildren().add(textField);
            Button button = new Button("Browse");
            button.setOnAction(event -> {
                File file = chooseResource(name.toLowerCase().contains("folder"));
                    if (file != null) {
                        textField.setText(file.getAbsolutePath());
                    }
            });
            wrapper.getChildren().add(button);
            return textField;
        }

        private File chooseResource(boolean isFolder) {
            if (isFolder) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Folder");
                return directoryChooser.showDialog(null);
            } else {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select File");
                return fileChooser.showOpenDialog(null);
            }
        }

        // SET PLACEHOLDER TEXT
        private String setTextualPrompt(String replacement) {
            return userString + " Used By: " + stepNames.toString().replace(",", replacement);
        }

        private List<String> createEnumOptions() {
            List<String> options = new LinkedList<>();

            switch (userString) {
                case "Operation type":
                    options.add("ZIP");
                    options.add("UNZIP");
                    break;
                case ("Method"):
                    options.add("GET");
                    options.add("POST");
                    options.add("PUT");
                    options.add("DELETE");
                    break;
                case ("protocol"):
                    options.add("HTTP");
                    options.add("HTTPS");
                    break;
                default:
                    throw(new IllegalArgumentException("No such enum in system. client InputController Line 190."));
            }

            return options;
        }

        public Node getInputFieldElementWithWrapper() {
            HBox wrapper = new HBox();
            wrapper.setAlignment(Pos.CENTER_LEFT);
            wrapper.setSpacing(10);
            Label label = new Label(userString + " [" + name.toLowerCase().replace('_',' ') + "]:");
            label.wrapTextProperty().set(true);
            wrapper.getChildren().add(label);
            wrapper.getChildren().add(inputFieldElement);
            HBox.setHgrow(inputFieldElement, Priority.ALWAYS);
            return wrapper;
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
//    @FXML ScrollPane inputsScrollPane;
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
        inputsVBox.setAlignment(Pos.TOP_CENTER);
        inputsVBox.setSpacing(15);
    }

    private void initializeButtonToolTip(){
        startFlowToolTip.setWrapText(true);
        startFlowToolTip.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 14));
        Tooltip.install(buttonWrapperForToolTip, startFlowToolTip);
        //startFlowToolTip.setShowDelay(Duration.ZERO);
        allMandatorySatisfied.addListener((observable, oldValue, newValue) -> {
            if(newValue){
                startFlowToolTip.setText("Click to start the flow");
            }else{
                startFlowToolTip.setText("To start, fill the mandatory inputs.\n" +
                        "If you want, optional inputs can be provided but are optional ;-)");
            }
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
            Platform.runLater(()-> {
                backToDefinitionButton.translateYProperty().set(4);
            });
        });
        backToDefinitionButton.setOnMouseReleased(event -> {
            Platform.runLater(() -> {
                backToDefinitionButton.translateYProperty().set(-4);
            });
        });

        backToDefinitionButton.setOnMouseClicked(event -> {
            Platform.runLater(() -> {
                inputPane.setVisible(false);
                definitionPane.setVisible(true);
            });
        });
    }

    public void initializeStartButton(ExecutionController executionController){
        ImageView iv = new ImageView(getClass().getResource("start-button.png").toString());
        iv.setFitHeight(160);
        iv.setFitWidth(160);
        iv.setPreserveRatio(true);
        Platform.runLater(() -> {
            startFlowButton.setGraphic(iv);
            startFlowButton.backgroundProperty().set(null);
        });
        startFlowButton.setOnMouseEntered((event) -> {
            Platform.runLater(() -> {
                startFlowButton.translateYProperty().set(2);
            });
        });
        startFlowButton.setOnMousePressed((event) -> {
            Platform.runLater(() -> {
                startFlowButton.translateYProperty().set(5);
            });

        });
        startFlowButton.setOnMouseExited((event) -> {
            Platform.runLater(() -> {
                startFlowButton.translateYProperty().set(-2);
            });
        });
        startFlowButton.setOnMouseReleased((event)-> {
            Platform.runLater(() -> {
                executionController.clearStepDetails();
                startFlowButton.translateYProperty().set(-5);
            });
        });
        allMandatorySatisfied.addListener((observable, oldValue, newValue) -> {
            if(newValue){
                Platform.runLater(() -> startFlowButton.setDisable(false));

            }else{
              Platform.runLater(() ->  startFlowButton.setDisable(true));
            }
        });
    }

    public void setInputsToSelectedFlow(FlowDefinitionDTO dto, Map<String,Object> continuationValues, Boolean isReRun) {
        inputsVBox.getChildren().clear();
        inputsLabel.setText("Inputs for " + dto.getFlowName());
        Pair<Map<INPUT_FIELDS,List>,Map<INPUT_FIELDS,List>> MandatoryAndOptionalFieldMaps =
                createMandatoryAndOptionalFieldMaps(dto);

        Map<INPUT_FIELDS, List> mandatoryFields = MandatoryAndOptionalFieldMaps.getKey();
        Map<INPUT_FIELDS, List> optionalFields = MandatoryAndOptionalFieldMaps.getValue();

        InputField.allFields.clear();

        if (mandatoryFields.get(INPUT_FIELDS.NAME).size() != 0) {
            setInputFieldElements(mandatoryFields, true, dto, continuationValues, isReRun);
        }
        if (optionalFields.get(INPUT_FIELDS.NAME).size() != 0) {
            setInputFieldElements(optionalFields, false, dto, continuationValues, isReRun);
        }
    }


    private void setInputFieldElements(Map<INPUT_FIELDS, List> fields, Boolean mandatory, FlowDefinitionDTO dto, Map<String,Object> continuationValues, Boolean isReRun) {
        VBox internalInputFieldVBox = new VBox();
        TitledPane InputsTitledPane = new TitledPane((mandatory ? "Mandatory Fields" : "Optional Fields"),internalInputFieldVBox);
        InputsTitledPane.setExpanded(false);
        inputsVBox.getChildren().add(InputsTitledPane);
        internalInputFieldVBox.setSpacing(15);


        for (int i = 0; i < fields.get(INPUT_FIELDS.NAME).size(); i++) {
            String inputName = (String) fields.get(INPUT_FIELDS.NAME).get(i);
            String inputType = (String) fields.get(INPUT_FIELDS.TYPE).get(i);
            String userString = (String) fields.get(INPUT_FIELDS.USER_STRING).get(i);
            Pair<String,List<String>> stepNames =(Pair) fields.get(INPUT_FIELDS.USED_BY).get(i);

            InputField inputElement = new InputField(inputName, inputType, userString, stepNames.getValue(), mandatory);
            inputElement.createElement(dto.getInitialValues(), continuationValues, isReRun);
            InputField.allFields.add(inputElement);
            if (!inputElement.isInitialValue) {
                internalInputFieldVBox.getChildren().add(inputElement.getInputFieldElementWithWrapper());
            }
        }
        if (mandatory){
            allMandatorySatisfied.bind(Bindings.createBooleanBinding(() ->
                    InputField.allFields.stream().filter(x -> x.isMandatory).allMatch(x -> x.satisfied.get()), InputField.allFields.stream().map(x -> x.satisfied).collect(Collectors.toList()).toArray(new BooleanProperty[0])));
        }
    }

    private static  Pair<Map<INPUT_FIELDS,List>,Map<INPUT_FIELDS,List>> createMandatoryAndOptionalFieldMaps(FlowDefinitionDTO dto) {
        List<Boolean> mandatoryInputsMask = dto.getFreeInputNecessity().stream()
                .map(x -> x.equals("MANDATORY"))
                .collect(Collectors.toList());
        List<String> freeInputTypes = dto.getFreeInputTypes();
        List<String> freeInputsFinalNames = dto.getFreeInputsFinalNames();
        List<String> freeInputUserStrings = dto.getFreeInputUserString();

        List<Pair<String,List<String>>> freeInputStepNames = dto.getFreeInputs2StepsThatUseThem();

        List<String> mandatoryInputs = freeInputsFinalNames.stream()
                .filter(x -> mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                .collect(Collectors.toList());

        List<String> optionalInputs = freeInputsFinalNames.stream()
                .filter(x -> !mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                .collect(Collectors.toList());

        List<String> mandatoryInputsTypes = freeInputsFinalNames.stream()
                .filter(x -> mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                .map(x -> freeInputTypes.get(freeInputsFinalNames.indexOf(x)))
                .collect(Collectors.toList());

        List<String> optionalInputsTypes = freeInputsFinalNames.stream()
                .filter(x -> !mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                .map(x -> freeInputTypes.get(freeInputsFinalNames.indexOf(x)))
                .collect(Collectors.toList());

        List<String> mandatoryInputsUserString = freeInputUserStrings.stream()
                .filter(x -> mandatoryInputsMask.get(freeInputUserStrings.indexOf(x)))
                .collect(Collectors.toList());

        List<String> optionalInputsUserString = freeInputsFinalNames.stream()
                .filter(x -> !mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                .map(x -> freeInputUserStrings.get(freeInputsFinalNames.indexOf(x)))
                .collect(Collectors.toList());

        List<Pair<String,List<String>>> mandatoryInputsStepNames = freeInputStepNames.stream()
                .filter(x -> mandatoryInputsMask.get(freeInputStepNames.indexOf(x)))
                .collect(Collectors.toList());

        List<Pair<String,List<String>>> optionalInputsStepNames = freeInputStepNames.stream()
                .filter(x -> !mandatoryInputsMask.get(freeInputStepNames.indexOf(x)))
                .collect(Collectors.toList());



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
        Map<String, Object> Name2Val = new HashMap<>();
        Map<String, String> Name2Type = new HashMap<>();

        try {
            InputField.allFields.forEach(inputField -> {
                String content = inputField.getContent();
                if (!content.equals("")) {
                    String inputName = inputField.getName();
                    String inputType = inputField.getType();
                    if (inputType.equals("Enum")) {
                        inputType = "Enumeration";
                    }
                    Name2Type.put(inputName, inputType);
                    Name2Val.put(inputName, finalizeInputByType(inputType, content));
                }
            });
        }
        catch(Exception e){
            System.out.println("it was the stream all alonng");
        }


        return new Pair<>(Name2Val, Name2Type);
    }

    private Object finalizeInputByType(String inputType, String content) {
        if (inputType.equals("Integer")) return Integer.parseInt(content);
        if (inputType.equals("Double")) return Float.parseFloat(content);
        return content;
    }

}

