package GUI.body.history;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import dto.execution.history.FlowsExecutionHistoryDTO;
import dto.execution.history.SingleFlowExecutionDTO;
import stepper.flow.execution.FlowExecutionResult;
import stepper.step.api.enums.StepResult;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;



public class HistoryController extends GUI.body.BodyControllerComponent implements Initializable{

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
        private ListView executedStepsStatusListView;

        @FXML
        private Label stepDetailsNameLabel;

        @FXML
        private Label stepDetailsDurationLabel;

        @FXML
        private Label stepDetailsResultLabel;

        @FXML
        private ListView logsListView;

        @FXML
        private ListView outputsListView;

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
        private ToggleGroup resFilters;
        @FXML
        private RadioButton successFilter;
        @FXML
        private RadioButton failureFilter;
        @FXML
        private RadioButton warningFilter;
        @FXML
        private RadioButton noneFilter;
        private ObservableList executedFlows;
        private FlowsExecutionHistoryDTO curFlowsExecutionHistoryDTO;
        private SingleFlowExecutionDTO selectedFlow;
        private RadioButton curFilteringElement = noneFilter;
        private Map<String , SingleStepExecutionTableData> currentFlowStepsExecutionTableDataMap = new LinkedHashMap<>();


        @Override
        public void initialize(URL location, ResourceBundle resources) {
                Platform.runLater(()-> {
                        flowName.setCellValueFactory(new PropertyValueFactory<SingleFlowExecutionDTO, String>("flowName"));
                        flowExecutionResult.setCellValueFactory(new PropertyValueFactory<SingleFlowExecutionDTO, FlowExecutionResult>("flowExecutionResult"));
                        startTime.setCellValueFactory(new PropertyValueFactory<SingleFlowExecutionDTO, String>("startTime"));
                        noneFilter.setSelected(true);
                });

                initializeHistoryTable();
                bindStepDetailsToSelectedStep();
                bindStepsListViewToSelectedFlow();
                bindSelectionOfOutputInListViewToOutputDetailsModal();
        }

        public static String getStepNameWithoutReadonly(String Name) {
                String finalItemName =  (Name.contains("(read-only)")) ?
                        Name.substring(0,Name.indexOf("(")-1)
                        :
                        Name;
                return finalItemName;
        }
        private void initializeHistoryTable() {
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

                resFilters.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                        if(oldValue != null) {
                                filterExecutions(oldValue, newValue);
                        }
                });

                historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                        if (newSelection != null && newSelection != oldSelection) {
                                this.selectedFlow = newSelection;
                        }
                });
        }

        private void filterExecutions(Toggle oldValue, Toggle newValue) {
                FlowExecutionResult filter;
                if (!oldValue.equals(newValue)) {
                        if (resFilters.getSelectedToggle() == successFilter) {
                                filter = FlowExecutionResult.SUCCESS;
                        } else if (resFilters.getSelectedToggle() == failureFilter) {
                                filter = FlowExecutionResult.FAILURE;
                        } else if (resFilters.getSelectedToggle() == warningFilter){
                                filter = FlowExecutionResult.WARNING;
                        } else {
                                filter = FlowExecutionResult.NONE;
                        }

                        executedFlows = FXCollections.observableArrayList(curFlowsExecutionHistoryDTO.filterFlowExecutionDTOsBy(filter));
                        Platform.runLater(() -> {
                                historyTable.setItems(executedFlows);
                        });
                }
        }




        public void updateTable(FlowsExecutionHistoryDTO flowsExecutionHistoryDTO) {
                curFlowsExecutionHistoryDTO = flowsExecutionHistoryDTO;
                executedFlows = FXCollections.observableArrayList(curFlowsExecutionHistoryDTO.getFlowExecutionDTOs());
                Platform.runLater(() -> {
                        historyTable.setItems(executedFlows);
                });
        }

        private void bindSelectionOfOutputInListViewToOutputDetailsModal() {
                outputsListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
                        Platform.runLater(() -> {
                                if(newValue != null) {

                                        Stage outputModal = new Stage();
                                        outputModal.setTitle("Outputs");
                                        outputModal.initModality(Modality.APPLICATION_MODAL);
                                        outputModal.initOwner(this.getBodyController().getMainController().getPrimaryStage());
                                        int outputidx = outputsListView.getSelectionModel().getSelectedIndex();
                                        String outputName = getStepNameWithoutReadonly(executedStepsStatusListView.getSelectionModel().getSelectedItem().toString());
                                        VBox currentOutputExecutionDataRoot = currentFlowStepsExecutionTableDataMap.get(outputName).getOutputNode(outputidx);
                                        Scene outputScene = new Scene(currentOutputExecutionDataRoot,200, 200);
                                        outputModal.setScene(outputScene);
                                        outputModal.sizeToScene();
                                        outputModal.showAndWait();
                                        outputsListView.getSelectionModel().clearSelection();
                                }
                        });

                }));
        }

        private void bindStepDetailsToSelectedStep() {
                executedStepsStatusListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                        if (newSelection != null && newSelection != oldSelection) {
                                SingleStepExecutionTableData selectedStepTableDataMap = currentFlowStepsExecutionTableDataMap.get(newSelection);
                                stepDetailsNameLabel.setText(selectedStepTableDataMap.getName());
                                stepDetailsDurationLabel.setText(getDurationOrNA(selectedStepTableDataMap));
                                stepDetailsResultLabel.setText(getResult(selectedStepTableDataMap));

                                logsListView.setItems(FXCollections.observableArrayList(selectedStepTableDataMap.getLogs()));
                                logsListView.setVisible(logsListView.getItems().size() > 0);

                                outputsListView.setItems(FXCollections.observableArrayList(selectedStepTableDataMap.getOutputsName()));
                                outputsListView.setVisible(outputsListView.getItems().size() > 0);
                        }
                });
        }

        private static String getResult(SingleStepExecutionTableData selectedStepTableDataMap) {
                try { return selectedStepTableDataMap.getResult().toString();}
                catch (NullPointerException e) { return "Not Executed";}
        }

        private static String getDurationOrNA(SingleStepExecutionTableData selectedStepTableDataMap) {
                try { return selectedStepTableDataMap.getDuration().toString();}
                catch (NullPointerException e) { return "N/A";}
        }

        private void bindStepsListViewToSelectedFlow() {
                historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                        if (newSelection != null && newSelection != oldSelection) {
                                currentFlowStepsExecutionTableDataMap.clear();
                                SingleFlowExecutionDTO selectedFlow = newSelection;
                                for (String stepName : selectedFlow.getFinalStepsName()) {
                                        StepResult result = selectedFlow.getStepExecutionResult(stepName);
                                        Duration duration = getDuration(selectedFlow, stepName);

                                        currentFlowStepsExecutionTableDataMap.put(stepName,
                                                new SingleStepExecutionTableData(stepName,
                                                        result,
                                                        duration,
                                                        selectedFlow.getStepLogs(stepName),
                                                        selectedFlow.getStepSummaryLine(stepName),
                                                        selectedFlow.getStepOutputs(stepName)));
                                }
                                Platform.runLater(()-> {
                                        executedStepsStatusListView.setItems(FXCollections.observableArrayList(currentFlowStepsExecutionTableDataMap.keySet()));
                                });

                        }
                });
        }

        private static Duration getDuration(SingleFlowExecutionDTO selectedFlow, String stepName) {
                try { return new Duration(selectedFlow.getStepDuration(stepName).toMillis()); }
                catch (Exception e) { return null;}
        }

}
