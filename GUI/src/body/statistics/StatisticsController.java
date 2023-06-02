package body.statistics;

import javafx.application.Platform;
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
        // CHECK THE AppController.numOfFlowExecutedProperty() and AppController.numOfFlowFinishedProperty() for the new code.
        // could be good to listen to it and update the chart accordingly.
        // OLD CODE HAPPENS ONCE.

//        if(EngineController.getInstance().executionOccurred()){
//            updateStepsCountChar();

    }
//    public void updateStepsDurationChar() {
//        // Reach engine through main controller...
//        StatisticsDTO statisticsDTO = EngineController.getInstance().getCurrentLoadedFlowsStatisticsDetails();
//        // { Original Step Name : (Occurrences Counter, Sum Durations) }
//        Map<String, Pair<Integer, Duration>> stepStatistics = statisticsDTO.getStepStatistics();
//
//        Platform.runLater(()->{
//
//            stepSumTimeChar.layout();
//
//            // steps count
//            XYChart.Series series1 = new XYChart.Series();
//
//            for(String stepName : stepStatistics.keySet()){
//                series1.getData().add(new XYChart.Data(stepName,stepStatistics.get(stepName).getKey()));
//            }
//            stepSumTimeChar.getData().clear();
//            stepSumTimeChar.getData().add(series1);
//        });
//    }

    public void updateBarChars() {
        // Reach engine through main controller...
        StatisticsDTO statisticsDTO = EngineController.getInstance().getCurrentLoadedFlowsStatisticsDetails();
        // { Original Step Name : (Occurrences Counter, Sum Durations) }
        Map<String, Pair<Integer, Duration>> stepStatistics = statisticsDTO.getStepStatistics();
        Map<String, Pair<Integer, Duration>> flowStatistics = statisticsDTO.getFlowStatistics();

        Platform.runLater(()-> {
            updateBarCharByName(stepStatistics, stepSumTimeChar, false);
            updateBarCharByName(stepStatistics, stepExecutionChar, true);
            updateBarCharByName(flowStatistics, flowSumTimeChar, false);
            updateBarCharByName(flowStatistics, flowExecutionChar, true);
        });
    }

    private void updateBarCharByName(Map<String, Pair<Integer, Duration>> statistics, BarChart<String,Number> curBarChar, Boolean isCountBased) {
            curBarChar.layout();

            XYChart.Series series1 = new XYChart.Series();

            for(String stepName : statistics.keySet()){
                series1.getData().add(new XYChart.Data(stepName, isCountBased ? statistics.get(stepName).getKey() : statistics.get(stepName).getValue().toMillis()));
            }
            curBarChar.getData().clear();
            curBarChar.getData().add(series1);
    }
}
