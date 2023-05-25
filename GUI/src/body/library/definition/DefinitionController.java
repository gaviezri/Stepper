package body.library.definition;
import app.AppController;
import body.library.LibraryControllerComponent;
import header.HeaderController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Pair;
import stepper.dto.flow.FlowDefinitionDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class DefinitionController extends LibraryControllerComponent {


    @FXML private ListView<String> flowDefAvailableFlowsList;
    @FXML private AnchorPane flowPresentationAnchorPane;
    @FXML private ScrollPane flowDataScrollPane;
    @FXML private Label selectedFlowNameLabel;
    @FXML private Label selectedFlowDescriptionLabel;

    @FXML private Button selectFlowButton;
    @FXML private Accordion flowInformationAccordion;
    @FXML private TitledPane stepsTitledPane;
    @FXML private ListView<String> stepsListView;
    @FXML private TitledPane inputsTitledPane;
    @FXML private TitledPane outputsTitledPane;
    private List<FlowDefinitionDTO> flowDefinitionDTOList = new ArrayList<>();
    private  SimpleListProperty<String> flowDescriptionsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private  SimpleListProperty <StringProperty> flowFormalOutputsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private  IntegerProperty selectedFlowIdx = new SimpleIntegerProperty(-1);
    private  List<List<String>> flowStepsFullNameByFlowIdx = new ArrayList<>();
    private void clearAllData() {
        flowDefAvailableFlowsList.getItems().clear();
        flowDescriptionsProperty.clear();
        flowFormalOutputsProperty.clear();
        flowStepsFullNameByFlowIdx.clear();

    }
    // --WIRINGS--
    public void initialize() {
        // set button photo
        initializeSelectButton();
        //hide steps in flow label until first flow is selected
        selectFlowButton.visibleProperty().set(false);
        flowInformationAccordion.visibleProperty().set(false);
        flowDataScrollPane.visibleProperty().set(false);
        // flow name label is binded to selected flow name
        selectedFlowNameLabel.textProperty().bind(Bindings.select(flowDefAvailableFlowsList.getSelectionModel().selectedItemProperty()));
        // selected flow index is binded to the index of the selected item in the list
        flowDefAvailableFlowsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            selectedFlowIdx.set(flowDefAvailableFlowsList.getSelectionModel().getSelectedIndex());
        });
        // label and stepview are bound to the selected flow index
        selectedFlowIdx.addListener(((observable, oldValue, newValue) -> {
            if(newValue!=null){

                if(!selectFlowButton.isVisible()) {
                    selectFlowButton.visibleProperty().set(true);
                }
                if(!flowInformationAccordion.isVisible()) {
                    flowInformationAccordion.visibleProperty().set(true);
                }
                if(!flowDataScrollPane.isVisible()) {
                    flowDataScrollPane.visibleProperty().set(true);
                }

                selectedFlowDescriptionLabel.textProperty().set(flowDescriptionsProperty.get(newValue.intValue()));
                stepsListView.getItems().clear();
                stepsListView.getItems().addAll(flowStepsFullNameByFlowIdx.get(newValue.intValue()));
            }
        }));



    }

    private void initializeSelectButton() {
        ImageView iv = new ImageView(getClass().getResource("select-button.png").toString());
        iv.setFitHeight(160);
        iv.setFitWidth(160);
        iv.setPreserveRatio(true);
        selectFlowButton.setGraphic(iv);
        selectFlowButton.backgroundProperty().set(null);
        selectFlowButton.setOnMousePressed(event -> {
            selectFlowButton.translateYProperty().set(3);
        });
        selectFlowButton.setOnMouseReleased(event -> {
            selectFlowButton.translateYProperty().set(-3);
        });
    }

    public void bindDefinitionTabComponents() {
        AppController mainController = this.libraryController.getBodyController().getMainController();
        HeaderController headerController = mainController.getHeaderController();
        headerController.getLoadedPath().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                clearAllData();
                flowDefinitionDTOList = mainController.getEngineController().getAllFlowDefinitionsData();

                for(FlowDefinitionDTO dto : flowDefinitionDTOList){
                    setFlowsHeadersData(dto);
                    setFlowsStepsData(dto);
                    setFlowInputsData(dto);
                    setFlowOutputsData(dto);
                }
            }
        });
    }
    // --FLOW HEADERS--
    private void setFlowsHeadersData(FlowDefinitionDTO dto) {
        flowDefAvailableFlowsList.getItems().add(dto.getFlowName());
        flowDescriptionsProperty.add(dto.getDescription());
        flowFormalOutputsProperty.addAll(dto.getFormalOutputs().stream()
                .map(SimpleStringProperty::new)
                .collect(Collectors.toList()));
    }
    // -- STEPS --
    private void setFlowsStepsData(FlowDefinitionDTO dto) {
        List<Boolean> stepsReadonly = dto.getIsStepReadonly();
        List<Pair<String,String>> step2alias = dto.getStep2alias();
        List<String> currentFlowStepsPresentations = new ArrayList<>();
        for (int i = 0; i<stepsReadonly.size(); ++i){
            Pair<String,String> s2a = step2alias.get(i);
            currentFlowStepsPresentations.add(stepPresentationFormat(s2a.getKey(),s2a.getValue(),stepsReadonly.get(i)));
        }
        flowStepsFullNameByFlowIdx.add(currentFlowStepsPresentations);
    }
    private String stepPresentationFormat(String name, String alias ,Boolean readonly) {;
        boolean gotAlias = !name.equals(alias);
        return alias +
                (gotAlias ? " - (" + name + ")" : "") +
                (readonly ? " [Readonly]" : "");
    }
    // --OUTPUTS--
    private void setFlowOutputsData(FlowDefinitionDTO dto) {
        List<String> outputs = dto.getOutputsFinalNames();
        List<String> outputTypes = dto.getOutputTypes();
        List<String> stepsThatUseOutputs = dto.getFinalStepNameThatProducedTheOutput();
        VBox content = new VBox();
        for (int i = 0; i < outputs.size(); i++) {
            content.getChildren().add(createOutputPane(outputs.get(i),
                    outputTypes.get(i),
                    stepsThatUseOutputs.get(i)));
        }
        outputsTitledPane.setContent(content);
    }
    private TitledPane createOutputPane(String name, String type, String stepProducedBy) {
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
            TitledPane presentation = createFreeInputPane(name, freeInputTypes.get(i),
                                                            stepsThatUseFreeInputs.get(i).getValue(),freeInputsNecessity.get(i));
            content.getChildren().add(presentation);
        }
        inputsTitledPane.setContent(content);
    }
    private TitledPane createFreeInputPane(String name, String type, List<String> stepsThatUseMe, String Necessity) {
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
                inputPane.setVisible(true);
                definitionPane.setVisible(false);
                this.libraryController.getInputComponentController().setInputsToSelectedFlow(flowDefinitionDTOList.get(flowDefAvailableFlowsList.getSelectionModel().getSelectedIndex()));
        });
    }

    public int getSelectedFlowIndex() {
        return flowDefAvailableFlowsList.getSelectionModel().getSelectedIndex();
    }
}
