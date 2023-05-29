package body.statistics;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.util.Pair;
import stepper.controller.EngineController;
import stepper.dto.statistics.StatisticsDTO;

import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.ResourceBundle;

public class StatisticsController extends body.BodyControllerComponent implements Initializable {
    @FXML private Tab stepSumTimeTab;
    @FXML private Tab flowExecutionCount;
    @FXML private Tab flowSumTimeTab;
    @FXML private Tab stepExecutionCount;

    @FXML private BarChart<String,Number> stepExecutionChar;
    @FXML private BarChart<String,Number> flowSumTimeChar;
    @FXML private BarChart<String,Number> flowExecutionChar;
    @FXML private BarChart<String,Number> stepSumTimeChar;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
//        stepSumTimeTab.setOnSelectionChanged(event -> {
//            if(stepSumTimeTab.isSelected()){
//                updateStepsCountChar();
//            }
//        });
//        flowExecutionCount.setOnSelectionChanged(event -> {
//            if(flowExecutionCount.isSelected()){
//                updateFlowExecutionCountChar();
//            }
//        });
//        flowSumTimeTab.setOnSelectionChanged(event -> {
//            if(flowSumTimeTab.isSelected()){
//                updateFlowSumTimeChar();
//            }
//        });
//        stepExecutionCount.setOnSelectionChanged(event -> {
//            if(stepExecutionCount.isSelected()){
//                updateStepExecutionCountChar();
//            }
//        });
    }

    // OLD CODE:
//        if(EngineController.getInstance().executionOccurred()){
//            updateStepsCountChar();
//        }


    private void updateStepsCountChar() {
        // Reach engine through main controller...
        StatisticsDTO statisticsDTO = EngineController.getInstance().getCurrentLoadedFlowsStatisticsDetails();
        // { Original Step Name : (Occurrences Counter, Sum Durations) }
        Map<String, Pair<Integer, Duration>> stepStatistics = statisticsDTO.getStepStatistics();

        stepSumTimeChar.getData().clear();
        stepSumTimeChar.layout();

        // steps count
        XYChart.Series series1 = new XYChart.Series();

        for(String stepName : stepStatistics.keySet()){
            series1.getData().add(new XYChart.Data(stepName,stepStatistics.get(stepName).getKey()));
        }
        stepSumTimeChar.getData().addAll(series1);
    }
}
