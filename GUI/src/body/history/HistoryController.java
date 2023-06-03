package body.history;

import javax.swing.event.ChangeListener;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
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
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.FlowExecutionResult;

import java.net.URL;
import java.util.List;
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
        private VBox RerunButton;
        ObservableList executedFlows;
        FlowsExecutionHistoryDTO curFlowsExecutionHistoryDTO;


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
                                List<Object> selectedFlowData = newSelection.getFlowData();
//                                System.out.println(selectedFlowData); TODO: make button not disable
                        }
                });
                RerunButton.setOnMouseEntered(event -> RerunButton.setLayoutY(RerunButton.getLayoutY()+10));
                RerunButton.setOnMouseExited(event -> RerunButton.setLayoutY(RerunButton.getLayoutY()-10));


                RerunButton.setOnMouseClicked(event -> {
                        // Perform your desired actions when the image is clicked
                        RerunButton.setLayoutY(RerunButton.getLayoutY()+5);
                        System.out.println("NOW I SHALL RE RUN AND GAL IS GAY");
                });
                RerunButton.setOnMouseReleased(event -> RerunButton.setLayoutY(RerunButton.getLayoutY()-5));
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