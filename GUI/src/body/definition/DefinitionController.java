package body.definition;
import app.AppController;
import body.BodyController;
import header.HeaderController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import stepper.dto.flow.FlowDefinitionDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class DefinitionController {



    BodyController bodyController;

    @FXML private ListView<String> flowDefAvailableFlowsList;
    @FXML private AnchorPane flowPresentationAnchorPane;
    @FXML private ScrollPane flowDataScrollPane;
    @FXML private Label selectedFlowNameLabel;
    @FXML private Label selectedFlowDescriptionLabel;

    @FXML private Button executeFlowButton;
    @FXML private Accordion flowInformationAccordion;
    @FXML private TitledPane stepsTitledPane;
    @FXML private ListView<String> stepsListView;
    @FXML private TitledPane inputsTitledPane;
    @FXML private TitledPane outputsTitledPane;

    private  SimpleListProperty<String> flowDescriptionsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private  SimpleListProperty <StringProperty> flowFormalOutputsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private  IntegerProperty selectedFlowIdx = new SimpleIntegerProperty(-1);
    private  List<List<String>> flowStepsFullNameByFlowIdx = new ArrayList<>();

    public void initialize() {
        //wirings
        //hide steps in flow label until first flow is selected
        executeFlowButton.visibleProperty().set(false);
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

                if(!executeFlowButton.isVisible()) {
                    executeFlowButton.visibleProperty().set(true);
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
    public void setBodyController(BodyController bodyController) {
        this.bodyController = bodyController;
    }

    private String stepPresentationFormat(String name, String alias ,Boolean readonly) {;
        boolean gotAlias = !name.equals(alias);
        return alias +
                (gotAlias ? " - (" + name + ")" : "") +
                 (readonly ? " [Readonly]" : "");
    }

    public void bindDefinitionTabComponents() {
        AppController mainController = this.bodyController.getMainController();
        HeaderController headerController = mainController.getHeaderController();
        headerController.getLoadedPath().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                clearAllData();
                List<FlowDefinitionDTO> allDTOs = mainController.getEngineController().getAllFlowDefinitionsData();

                for(FlowDefinitionDTO dto : allDTOs){
                    setFlowsHeadersData(dto);
                    setFlowsStepsData(dto);
                    setFlowInputsData(dto);


                }

            }
        });
    }

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
            TitledPane presentation = createFreeInputPane(name,
                    freeInputTypes.get(i),
                    stepsThatUseFreeInputs.get(i).getValue(),
                    freeInputsNecessity.get(i));
            content.getChildren().add(presentation);
        }
        inputsTitledPane.setContent(new ScrollPane(content));
    }

    private void setFlowsStepsData(FlowDefinitionDTO dto) {
        List<Boolean> stepsReadonly = dto.getIsStepReadonly();
        List<Pair<String,String>> step2alias = dto.getStep2alias();
        List<String> currentFlowStepsPanes = new ArrayList<>();
        for (int i = 0; i<stepsReadonly.size(); ++i){
            Pair<String,String> s2a = step2alias.get(i);
            currentFlowStepsPanes.add(stepPresentationFormat(s2a.getKey(),s2a.getValue(),stepsReadonly.get(i)));
        }
        flowStepsFullNameByFlowIdx.add(currentFlowStepsPanes);
    }

    private void setFlowsHeadersData(FlowDefinitionDTO dto) {
        flowDefAvailableFlowsList.getItems().add(dto.getFlowName());
        flowDescriptionsProperty.add(dto.getDescription());
        flowFormalOutputsProperty.addAll(dto.getFormalOutputs().stream()
                .map(SimpleStringProperty::new)
                .collect(Collectors.toList()));
    }

    private void clearAllData() {
        flowDefAvailableFlowsList.getItems().clear();
        flowDescriptionsProperty.clear();
        flowFormalOutputsProperty.clear();
        flowStepsFullNameByFlowIdx.clear();

    }

    private TitledPane createFreeInputPane(String name, String type, List<String> stepsThatUseMe, String Necessity) {
        TitledPane inputPane = new TitledPane();
        inputPane.setText(name);
        inputPane.setExpanded(false);

        VBox content = new VBox();
        ScrollPane contentScrollPane = new ScrollPane();
        contentScrollPane.setContent(content);

        Label necessityLabel = new Label("Necessity: " + Necessity.toLowerCase());
        Label typeLabel = new Label("Type: " + type.toLowerCase());
        TitledPane usedBy = new TitledPane();
        usedBy.setText("Used by (steps)");
        usedBy.setContent(new ListView<>(FXCollections.observableArrayList(stepsThatUseMe)));

        inputPane.setContent(contentScrollPane);
        content.getChildren().addAll(typeLabel, necessityLabel, usedBy);

        return inputPane;
    }
}
