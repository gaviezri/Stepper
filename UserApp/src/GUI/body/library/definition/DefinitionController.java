package GUI.body.library.definition;

import GUI.body.library.LibraryControllerComponent;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import dto.flow.FlowDefinitionDTO;
import dto.step.SingleStepDTO;
import dto.step.StepsDTO;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class DefinitionController extends LibraryControllerComponent {
    @FXML private ListView<String> flowDefAvailableFlowsList;
    @FXML private AnchorPane flowPresentationAnchorPane;
    @FXML private ScrollPane flowDataScrollPane;
    @FXML private Label selectedFlowNameLabel;
    @FXML private Label selectedFlowDescriptionLabel;
    @FXML private Label continuationsLabel;
    @FXML private Button selectFlowButton;
    @FXML private Accordion flowInformationAccordion;
    @FXML private TitledPane stepsTitledPane;
    @FXML private TitledPane inputsTitledPane;
    @FXML private TitledPane outputsTitledPane;
    private List<FlowDefinitionDTO> flowDefinitionDTOList = new ArrayList<>();
    private final SimpleListProperty<String> flowDescriptionsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final SimpleListProperty <StringProperty> flowFormalOutputsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty selectedFlowIdx = new SimpleIntegerProperty(-1);
    private final List<VBox> stepsVBoxByFlowIdx = new ArrayList<>();
    private final List<VBox> inputsVBoxByFlowIdx = new ArrayList<>();
    private final List<VBox> outputsVBoxByFlowIdx = new ArrayList<>();
    public ListView<String> getFlowDefAvailableFlowsList() {
        return flowDefAvailableFlowsList;
    }
    private void clearAllData() {
        Platform.runLater(() -> {
            try {
                flowDefAvailableFlowsList.getItems().clear();
                flowDescriptionsProperty.clear();
                flowFormalOutputsProperty.clear();
                stepsVBoxByFlowIdx.clear();
                inputsVBoxByFlowIdx.clear();
                outputsVBoxByFlowIdx.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public TitledPane getStepsTitledPane() {
        return stepsTitledPane;
    }
    // --WIRINGS--
    public void initialize() {
        // set button photo
        initializeSelectButton();
        initializeContinuationsLabel();
        //hide steps in flow label until first flow is selected
        selectFlowButton.visibleProperty().set(false);
        flowInformationAccordion.visibleProperty().set(false);
        flowDataScrollPane.visibleProperty().set(false);
        // flow name label is binded to selected flow name
        selectedFlowNameLabel.textProperty().bind(Bindings.select(flowDefAvailableFlowsList.getSelectionModel().selectedItemProperty()));
        // selected flow index is binded to the index of the selected item in the list
        flowDefAvailableFlowsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            selectedFlowIdx.set(getSelectedFlowIndex());
        });
        // label and stepview are bound to the selected flow index
        selectedFlowIdx.addListener(((observable, oldValue, newValue) -> {
            if(newValue!=null && newValue.intValue() > -1) {

                if(!selectFlowButton.isVisible()) {
                    selectFlowButton.visibleProperty().set(true);
                }
                if(!flowInformationAccordion.isVisible()) {
                    flowInformationAccordion.visibleProperty().set(true);
                }
                if(!flowDataScrollPane.isVisible()) {
                    flowDataScrollPane.visibleProperty().set(true);
                }
                int theIdx = newValue.intValue();
                selectedFlowDescriptionLabel.textProperty().set(flowDescriptionsProperty.get(theIdx));
                stepsTitledPane.setContent(stepsVBoxByFlowIdx.get(theIdx));
                inputsTitledPane.setContent(inputsVBoxByFlowIdx.get(theIdx));
                outputsTitledPane.setContent(outputsVBoxByFlowIdx.get(theIdx));
            }
        }));
    }

    private void initializeContinuationsLabel(){
        continuationsLabel.setVisible(false);
        continuationsLabel.visibleProperty().bind(selectedFlowIdx.greaterThan(-1));
        continuationsLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            if(selectedFlowIdx.get() > -1) {
                return "Continuations: " + flowDefinitionDTOList.get(selectedFlowIdx.get()).getContinuationsCount();
            }
            return "";
        }, selectedFlowIdx));

        continuationsLabel.setWrapText(true);
    }
    private void initializeSelectButton() {
        ImageView iv = new ImageView(getClass().getResource("select-button.png").toString());
        iv.setFitHeight(160);
        iv.setFitWidth(160);
        iv.setPreserveRatio(true);
        selectFlowButton.setGraphic(iv);
        selectFlowButton.backgroundProperty().set(null);
        selectFlowButton.setOnMouseEntered(event -> {
            selectFlowButton.translateYProperty().set(2);
        });
        selectFlowButton.setOnMousePressed(event -> {
            selectFlowButton.translateYProperty().set(5);
        });
        selectFlowButton.setOnMouseReleased(event -> {
            selectFlowButton.translateYProperty().set(-2);
        });
        selectFlowButton.setOnMouseExited(event -> {
            selectFlowButton.translateYProperty().set(-5);
        });
    }



    public void updateFlowDefinitions(List<FlowDefinitionDTO> flowDefDTOlist) {
            if (newFlowEqualsNamewise(flowDefDTOlist)) {
                updateAnyContinuationAvailableBasedOnRoleModification(flowDefDTOlist);
                return;
            } else {
                clearGUIelements();
            }
            clearAllData();
            flowDefinitionDTOList = flowDefDTOlist;
            // add a reset method
            for(FlowDefinitionDTO dto : flowDefinitionDTOList){
                setFlowsHeadersData(dto);
                setFlowsStepsData(dto);
                setFlowInputsData(dto);
                setFlowOutputsData(dto);
            }
    }

    private void clearGUIelements() {
        Platform.runLater(() -> {
            flowDataScrollPane.visibleProperty().set(false);
            selectFlowButton.visibleProperty().setValue(false);
            selectedFlowDescriptionLabel.setText("");
        });
    }

    private void updateAnyContinuationAvailableBasedOnRoleModification(List<FlowDefinitionDTO> flowDefDTOlist) {
        for(int i = 0; i < flowDefDTOlist.size(); i++) {
            FlowDefinitionDTO dto = flowDefDTOlist.get(i);
            FlowDefinitionDTO oldDto = flowDefinitionDTOList.get(i);
            if(dto.getContinuationsCount() != oldDto.getContinuationsCount()) {
                Platform.runLater(() -> {
                    continuationsLabel.textProperty().set("Continuations: " + dto.getContinuationsCount());
                });
            }
        }
    }

    private boolean newFlowEqualsNamewise(List<FlowDefinitionDTO> flowDefDTOlist) {
        return flowDefDTOlist.stream().map(FlowDefinitionDTO::getFlowName).collect(Collectors.toList())
                .equals(flowDefinitionDTOList.stream().map(FlowDefinitionDTO::getFlowName).collect(Collectors.toList()));
    }

    // --FLOW HEADERS--
    private void setFlowsHeadersData(FlowDefinitionDTO dto) {
        Platform.runLater(()->{
            flowDefAvailableFlowsList.getItems().add(dto.getFlowName());
            flowDescriptionsProperty.add(dto.getDescription());
            flowFormalOutputsProperty.addAll(dto.getFormalOutputs().stream()
                    .map(SimpleStringProperty::new)
                    .collect(Collectors.toList()));
        });
    }
    // -- STEPS --
    private void setFlowsStepsData(FlowDefinitionDTO dto) {
        List<Boolean> stepsReadonly = dto.getIsStepReadonly();
        VBox content = new VBox();
        StepsDTO allStepsDTO = dto.getStepsDTO();

        for (SingleStepDTO stepDTO : allStepsDTO.getSteps()) {
            content.getChildren().size();
            content.getChildren().add(createStepPane(stepDTO, stepsReadonly.get(content.getChildren().size())));
        }
        Platform.runLater(()->{
            stepsVBoxByFlowIdx.add(content);
        });
    }
    private TitledPane createStepPane(SingleStepDTO stepDTO, Boolean isReadOnly) {
        TitledPane stepPane = new TitledPane();
        stepPane.setText(stepDTO.getStepName() + (isReadOnly ? " (read-only)" : ""));
        stepPane.setExpanded(false);
        SplitPane content = new SplitPane();
        VBox wrapper = new VBox();
        wrapper.getChildren().add(content);
        content.setOrientation(Orientation.HORIZONTAL);
        stepPane.setContent(wrapper);
        content.getItems().add(createStepInputsPane(stepDTO));
        content.getItems().add(createStepOutputsPane(stepDTO));
        content.setDividerPosition(0, 0.5);
        return stepPane;
    }
    private Node createStepInputsPane(SingleStepDTO stepDTO) {
        TitledPane inputsPane = new TitledPane();
        inputsPane.setText("Inputs");
        inputsPane.setExpanded(true);
        inputsPane.setCollapsible(false);
        VBox content = new VBox();
        inputsPane.setContent(content);

        List<String> inputsFinalNames = stepDTO.getInputsFinalNames();
        List<Boolean> isMandatoryInput = stepDTO.getIsMandatoryInput();
        List<Boolean> isConnectedInput = stepDTO.getIsConnectedInput();
        Map<String,Pair<String,String>> inputFinalName2SourceStepAndOutPut = stepDTO.getInputPostAliasName2SourceStepNameAndSourceOutputName();

        for (int i = 0; i < inputsFinalNames.size(); i++) {
            String finalName = inputsFinalNames.get(i);
            content.getChildren().add(createStepInputPane(finalName,
                                                        isMandatoryInput.get(i),
                                                        isConnectedInput.get(i),
                                                        inputFinalName2SourceStepAndOutPut.get(finalName)));
        }
        return inputsPane;
    }

    private Node createStepInputPane(String name, Boolean mandatory, Boolean connected, Pair<String,String> SourceStepAndSourceOutPut){
        TitledPane inputPane = new TitledPane();
        inputPane.setText(name);
        inputPane.setExpanded(true);

        VBox content = new VBox();
        inputPane.setContent(content);

        Label mandatoryLabel = new Label("Mandatory: " + mandatory);
        Label connectedLabel = new Label("Connected: " + connected);
        if (connected) {
            Label sourceStepLabel = new Label("from (source step): " + SourceStepAndSourceOutPut.getKey());
            Label sourceOutputLabel = new Label("output of: " + SourceStepAndSourceOutPut.getValue());
            content.getChildren().addAll(mandatoryLabel, connectedLabel, sourceStepLabel, sourceOutputLabel);
        } else {
            content.getChildren().addAll(mandatoryLabel, connectedLabel);
        }
        return inputPane;
    }

    private Node createStepOutputsPane(SingleStepDTO stepDTO) {
        TitledPane outputsPane = new TitledPane();
        outputsPane.setText("Outputs");
        outputsPane.setExpanded(true);
        outputsPane.setCollapsible(false);
        VBox content = new VBox();
        outputsPane.setContent(content);

        List<String> outputsFinalNames = stepDTO.getOutputsNamesPostAliasing();
        Map<String,List<Pair<String,String>>> outputTargets = stepDTO.getOutputPostAliasName2AllTargetStepNameAndTargetInputName();
        for (int i = 0; i < outputsFinalNames.size(); i++) {
            String finalName = outputsFinalNames.get(i);
            content.getChildren().add(createSingleStepOutputPane(finalName, outputTargets.get(finalName)));
        }
        return outputsPane;
    }

    private Node createSingleStepOutputPane(String finalName, List<Pair<String, String>> targetStepAndInputName) {
        TitledPane outputPane = new TitledPane();
        outputPane.setText(finalName);
        outputPane.setExpanded(true);


        VBox content = new VBox();
        outputPane.setContent(content);
        if (targetStepAndInputName != null) {
            for (Pair<String, String> target : targetStepAndInputName) {
                Label targetStepLabel = new Label("to (target step): " + target.getKey());
                Label targetInputLabel = new Label("input of: " + target.getValue());
                content.getChildren().addAll(targetStepLabel, targetInputLabel);
            }
        }
        return outputPane;
    }

    // --OUTPUTS--
    private void setFlowOutputsData(FlowDefinitionDTO dto) {
        List<String> outputs = dto.getOutputsFinalNames();
        List<String> outputTypes = dto.getOutputTypes();
        List<String> stepsThatUseOutputs = dto.getFinalStepNameThatProducedTheOutput();
        VBox content = new VBox();
        for (int i = 0; i < outputs.size(); i++) {
            content.getChildren().add(createFlowOutputPane(outputs.get(i),
                    outputTypes.get(i),
                    stepsThatUseOutputs.get(i)));
        }
        outputsVBoxByFlowIdx.add(content);
    }
    private TitledPane createFlowOutputPane(String name, String type, String stepProducedBy) {
        TitledPane outputPane = new TitledPane();
        outputPane.setText(name);
        outputPane.setExpanded(false);

        VBox content = new VBox();
        outputPane.setContent(content);
        Label typeLabel = new Label("Type: " + type);
        Label stepProducedByLabel = new Label("Produced by (Step): " + stepProducedBy);
        content.getChildren().addAll(typeLabel,stepProducedByLabel);
        return outputPane;
    }
    // --INPUTS--
    private void setFlowInputsData(FlowDefinitionDTO dto) {

        List<String> freeInputs = dto.getFreeInputsFinalNames();
        List<String> freeInputTypes = dto.getFreeInputTypes();
        List<Pair<String,List<String>>> stepsThatUseFreeInputs = dto.getFreeInputs2StepsThatUseThem();
        List<String> freeInputsNecessity = dto.getFreeInputNecessity();
        Set<String> usedNames = new HashSet<>();
        VBox content = new VBox();
        for (int i = 0; i < freeInputs.size(); i++) {
            String name = freeInputs.get(i);
            if (usedNames.contains(name)) {
                continue;
            }
            usedNames.add(name);
            TitledPane presentation = createFlowFreeInputPane(name, freeInputTypes.get(i),
                                                            stepsThatUseFreeInputs.get(i).getValue(),freeInputsNecessity.get(i));
            content.getChildren().add(presentation);
        }
        inputsVBoxByFlowIdx.add(content);
    }
    private TitledPane createFlowFreeInputPane(String name, String type, List<String> stepsThatUseMe, String Necessity) {
        TitledPane inputPane = new TitledPane();
        inputPane.setText(name);
        inputPane.setExpanded(false);

        VBox content = new VBox();

        Label usedBy = new Label();
        usedBy.setText("Used by (steps):");
        usedBy.setUnderline(true);

        List<Label> stepsThatUseMeLabels = stepsThatUseMe.stream().map(Label::new).collect(Collectors.toList());

        inputPane.setContent(content);
        content.getChildren().addAll( new Label("Type: " + type.toLowerCase()),
                                        new Label("Necessity: " + Necessity.toLowerCase()),
                                            usedBy);
        content.getChildren().addAll(stepsThatUseMeLabels);

        return inputPane;
    }

    public void bindInputPaneEnablementToSelectButton(AnchorPane inputPane, AnchorPane definitionPane) {
        selectFlowButton.setOnMouseClicked(event -> {
            Platform.runLater(() -> {
                inputPane.setVisible(true);
                definitionPane.setVisible(false);
                libraryController.setInputsToSelectedFlow(flowDefinitionDTOList.get(getSelectedFlowIndex()),
                        null,
                        false);
            });
        });
    }

    public int getSelectedFlowIndex() {
        return flowDefAvailableFlowsList.getSelectionModel().getSelectedIndex();
    }

    public String getSelectedFlowName(){
        return flowDefAvailableFlowsList.getSelectionModel().getSelectedItem();
    }
    public void setContinuationFlowInputs(String flowNameContinuedTo, List<Pair<String,String>> output2InputMapping) {
        OptionalInt index = IntStream.range(0, flowDefinitionDTOList.size())
                .filter(i -> flowDefinitionDTOList.get(i).getFlowName().equals(flowNameContinuedTo))
                .findFirst();
        if (index.isPresent()) {

            Map<String,Object> lastFlowOutputs = libraryController.getBodyController().getMainController().getLastFlowOutputs();
            Map<String,Object> inputsToSet = new HashMap<>();
            if (lastFlowOutputs != null ) {
                for (Pair<String,String> output2Input : output2InputMapping) {
                    String outputName = output2Input.getKey();
                    String inputName = output2Input.getValue();
                    Object outputValue = lastFlowOutputs.get(outputName);
                    if (outputValue == null) {
                        continue;
                    }
                    inputsToSet.put(inputName, outputValue);
                }
            }
            flowDefAvailableFlowsList.getSelectionModel().select(index.getAsInt());
            libraryController.getInputComponentController().setInputsToSelectedFlow(flowDefinitionDTOList.get(index.getAsInt()),inputsToSet,false);
        }
    }

    public FlowDefinitionDTO getFlowDefinitionsDataByIndex(int flowIndex) {
        return flowDefinitionDTOList.get(flowIndex);
    }

    public FlowDefinitionDTO getFlowDefinitionsDataByName(String selectedFlowName) {
        return flowDefinitionDTOList.stream().filter(flow -> flow.getFlowName().equals(selectedFlowName)).findFirst().orElse(null);
    }

    public void setSelectedFlowByName(String flowName) {
        OptionalInt index = IntStream.range(0, flowDefinitionDTOList.size())
                .filter(i -> flowDefinitionDTOList.get(i).getFlowName().equals(flowName))
                .findFirst();
        if (index.isPresent()) {
            flowDefAvailableFlowsList.getSelectionModel().select(index.getAsInt());
        }
    }
}
