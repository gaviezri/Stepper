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
import javafx.util.Pair;
import stepper.dto.flow.FlowDefinitionDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DefinitionController {



    BodyController bodyController;

    @FXML private ListView<String> flowDefAvailableFlowsList;
    @FXML private AnchorPane flowPresentationAnchorPane;

    @FXML private Label selectedFlowNameLabel;
    @FXML private Label selectedFlowDescriptionLabel;

    @FXML private Button executeFlowButton;
    @FXML private Accordion flowInformationAccordion;
    @FXML private TitledPane stepsTitledPane;
    @FXML private ListView<String> stepsListView;
    @FXML private TitledPane inputsTitledPane;
    @FXML private ListView<String> inputsListView;
    @FXML private TitledPane outputsTitledPane;
    @FXML private ListView<String> outputsListView;

    private  SimpleListProperty<String> flowDescriptionsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private  SimpleListProperty <StringProperty> flowFormalOutputsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private  IntegerProperty selectedFlowIdx = new SimpleIntegerProperty(-1);
    private  List<List<String>> flowStepsFullNameByFlowIdx = new ArrayList<>();

    public void initialize() {
        //wirings
        //hide steps in flow label until first flow is selected
        executeFlowButton.visibleProperty().set(false);
        flowInformationAccordion.visibleProperty().set(false);
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


                }

            }
        });
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
}
