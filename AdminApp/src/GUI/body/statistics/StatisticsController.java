package GUI.body.statistics;

import GUI.body.BodyControllerComponent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.util.Pair;
import dto.statistics.StatisticsDTO;

import java.time.Duration;
import java.util.Map;

public class StatisticsController extends BodyControllerComponent {
    @FXML private Tab stepSumTimeTab;
    @FXML private Tab flowExecutionCount;
    @FXML private Tab flowSumTimeTab;
    @FXML private Tab stepExecutionCount;

    @FXML private BarChart<String,Number> stepExecutionChart;
    @FXML private BarChart<String,Number> flowSumTimeChart;
    @FXML private BarChart<String,Number> flowExecutionChart;
    @FXML private BarChart<String,Number> stepSumTimeChart;

    private Map<String, Pair<Integer,Duration>> stepStatistics;
    private Map<String, Pair<Integer,Duration>> flowStatistics;

    public void updateBarChars(StatisticsDTO statisticsDTO){
        // Reach engine through main controller...

        // { Original Step Name : (Occurrences Counter, Sum Durations) }
        Platform.runLater(()-> {
            Map<String, Pair<Integer, Duration>> newStepStatistics = statisticsDTO.getStepStatistics();
            Map<String, Pair<Integer, Duration>> newFlowStatistics = statisticsDTO.getFlowStatistics();
            if (FoundDeltaFromLastUpdate(newStepStatistics, false)) {
                updateBarChartByName(newStepStatistics, stepSumTimeChart, false);
                updateBarChartByName(newFlowStatistics, stepExecutionChart, true);
            }
            if (FoundDeltaFromLastUpdate(newFlowStatistics, true)){
                updateBarChartByName(newFlowStatistics, flowSumTimeChart, false);
                updateBarChartByName(newFlowStatistics, flowExecutionChart, true);
            }
            stepStatistics = newStepStatistics;
            flowStatistics = newFlowStatistics;
        });


    }

    private void updateBarChartByName(Map<String, Pair<Integer, Duration>> statistics, BarChart<String,Number> curBarChart, Boolean isCountBased) {
            curBarChart.layout();

            XYChart.Series series1 = new XYChart.Series();

            for(String name : statistics.keySet()){
                series1.getData().add(new XYChart.Data(name, isCountBased ? statistics.get(name).getKey() : statistics.get(name).getValue().toMillis()));
            }
            curBarChart.getData().clear();
            curBarChart.getData().add(series1);
    }

    private boolean FoundDeltaFromLastUpdate(Map<String,Pair<Integer,Duration>> newStatistics, boolean isFlowStatistics) {

        Map<String,Pair<Integer,Duration>> oldStatistics = isFlowStatistics ? flowStatistics : stepStatistics;
        if (oldStatistics == null) {
            return true;
        }
        for(Map.Entry<String,Pair<Integer,Duration>> entry : newStatistics.entrySet()){
            if (!oldStatistics.containsKey(entry.getKey())) {
                return true;
            }
            if (!oldStatistics.get(entry.getKey()).getKey().equals(entry.getValue().getKey())) {
                return true;
            }
            if (!oldStatistics.get(entry.getKey()).getValue().equals(entry.getValue().getValue())) {
                return true;
            }
        }
        return false;

    }
}
