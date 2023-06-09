package body.history;

import body.BodyController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import stepper.dto.execution.history.FlowsExecutionHistoryDTO;
import stepper.dto.execution.history.SingleFlowExecutionDTO;
import stepper.dto.flow.FlowDefinitionDTO;
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.FlowExecutionResult;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

public class HistoryController extends body.BodyControllerComponent implements Initializable{

        @FXML
        private AnchorPane flowHistoryComponent;
        @FXML
        private TableView<SingleFlowExecutionDTO> historyTable;

        @FXML
        private TableColumn<SingleFlowExecutionDTO, String> flowName;

        @FXML
        private TableColumn<SingleFlowExecutionDTO, FlowExecutionResult> flowExecutionResult;

        @FXML
        private TableColumn<SingleFlowExecutionDTO, String> startTime;

        @FXML
        private ToggleGroup SortFilters;
        @FXML
        private RadioButton startTimeFilter;
        @FXML
        private RadioButton resultFilter;
        @FXML
        private RadioButton nameFilter;
        private FlowsExecutionHistoryDTO.SortFilter curSortingFilter = FlowsExecutionHistoryDTO.SortFilter.TIME;
        @FXML
        private VBox rerunButton;
        private ObservableList executedFlows;
        private FlowsExecutionHistoryDTO curFlowsExecutionHistoryDTO;
        private SingleFlowExecutionDTO selectedFlow;

        public void updateTable(Stack<FlowExecution> flowExecutionStack){
                curFlowsExecutionHistoryDTO = new FlowsExecutionHistoryDTO(flowExecutionStack);
                executedFlows = FXCollections.observableArrayList(curFlowsExecutionHistoryDTO.getFlowExecutionDTOs());
                Platform.runLater(()-> {
                        historyTable.setItems(executedFlows);
                });
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
                Platform.runLater(()-> {
                        flowName.setCellValueFactory(new PropertyValueFactory<SingleFlowExecutionDTO, String>("flowName"));
                        flowExecutionResult.setCellValueFactory(new PropertyValueFactory<SingleFlowExecutionDTO, FlowExecutionResult>("flowExecutionResult"));
                        startTime.setCellValueFactory(new PropertyValueFactory<SingleFlowExecutionDTO, String>("startTime"));
                });

                SortFilters.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                        if (SortFilters.getSelectedToggle() != null) {
                                if (SortFilters.getSelectedToggle() == resultFilter) {
                                        curFlowsExecutionHistoryDTO.sortFlowExecutionDTOsBy(FlowsExecutionHistoryDTO.SortFilter.RESULT);
                                } else if (SortFilters.getSelectedToggle() == nameFilter) {
                                        curFlowsExecutionHistoryDTO.sortFlowExecutionDTOsBy(FlowsExecutionHistoryDTO.SortFilter.NAME);
                                } else {
                                       curFlowsExecutionHistoryDTO.sortFlowExecutionDTOsBy(FlowsExecutionHistoryDTO.SortFilter.TIME);
                                }
                                executedFlows = FXCollections.observableArrayList(curFlowsExecutionHistoryDTO.getFlowExecutionDTOs());
                                Platform.runLater(()-> {
                                        historyTable.setItems(executedFlows);
                                });
                        }
                });

                historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                        if (newSelection != null && newSelection != oldSelection) {
                                this.selectedFlow = newSelection;
                                Platform.runLater(() -> {
                                        rerunButton.setDisable(false);
                                        rerunButton.setOpacity(1.0);
                                });
                        }
                        else{
                                Platform.runLater(() -> {
                                        rerunButton.setDisable(true);
                                        rerunButton.setOpacity(0.5);

                                });
                        }
                });

                Platform.runLater(()->{
                        rerunButton.setDisable(true);
                        rerunButton.setOpacity(0.5);

                });
                rerunButton.setOnMouseEntered(event ->
                        Platform.runLater(()-> rerunButton.setLayoutY(rerunButton.getLayoutY()+10)
                        ));

                rerunButton.setOnMouseExited(event ->
                        Platform.runLater(()-> rerunButton.setLayoutY(rerunButton.getLayoutY()-10)
                                ));

                rerunButton.setOnMouseReleased(event ->
                        Platform.runLater(()-> rerunButton.setLayoutY(rerunButton.getLayoutY()-5))
                        );

        }

        public void bindInputPaneEnablementToReRunButton(AnchorPane inputPane, AnchorPane definitionPane ) {
                rerunButton.setOnMouseClicked(event -> {
                        Platform.runLater(()->{
                                rerunButton.setLayoutY(rerunButton.getLayoutY()+5);
                                bodyController.setActiveTab(BodyController.FLOW_LIB_TAB);
                                inputPane.setVisible(true);
                                definitionPane.setVisible(false);
                                bodyController.getFlowLibComponentController().
                                        getInputComponentController().
                                        setInputsToSelectedFlow(
                                                bodyController.getMainController().getEngineController().
                                                        getFlowDefinitionDtoByName(selectedFlow.getFlowName()),
                                                selectedFlow.getDataName2value(),true);

                        });
                });
        }
}

//        public void setCurSortingFilter(ObservableValue<? extends Toggle> ov,
//                                        Toggle old_toggle, Toggle new_toggle) {
//                if (SortFilters.getSelectedToggle() != null) {
//                        if (SortFilters.getSelectedToggle() == resultFilter) {
//                                curSortingFilter = FlowsExecutionHistoryDTO.SortFilter.RESULT;
//                        } else {
//                                curSortingFilter = SortFilters.getSelectedToggle() == nameFilter ?
//                                        FlowsExecutionHistoryDTO.SortFilter.NAME :
//                                        FlowsExecutionHistoryDTO.SortFilter.TIME;
//                        }
//                }
//        }