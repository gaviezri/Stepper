package body.history;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import stepper.dto.execution.history.FlowsExecutionHistoryDTO;
import stepper.dto.execution.history.SingleFlowExecutionDTO;
import stepper.dto.flow.ExecutedFlowDetailsDTO;
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.FlowExecutionResult;

import java.net.URL;
import java.util.Observable;
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

        ObservableList executedFlows;


        public void updateTable(Stack<FlowExecution> flowExecutionStack){
                FlowsExecutionHistoryDTO flowsExecutionHistoryDTO = new FlowsExecutionHistoryDTO(flowExecutionStack);
                executedFlows = FXCollections.observableArrayList(flowsExecutionHistoryDTO.getFlowExecutionDTOs());
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
        }
}

