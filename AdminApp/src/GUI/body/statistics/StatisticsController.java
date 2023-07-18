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

    @FXML private BarChart<String,Number> stepExecutionChar;
    @FXML private BarChart<String,Number> flowSumTimeChar;
    @FXML private BarChart<String,Number> flowExecutionChar;
    @FXML private BarChart<String,Number> stepSumTimeChar;

    public void updateBarChars(StatisticsDTO statisticsDTO){
        // Reach engine through main controller...

        // { Original Step Name : (Occurrences Counter, Sum Durations) }
        Map<String, Pair<Integer, Duration>> stepStatistics = statisticsDTO.getStepStatistics();
        Map<String, Pair<Integer, Duration>> flowStatistics = statisticsDTO.getFlowStatistics();

        Platform.runLater(()-> {
            try {
                updateBarCharByName(stepStatistics, stepSumTimeChar, false);
                updateBarCharByName(stepStatistics, stepExecutionChar, true);
                updateBarCharByName(flowStatistics, flowSumTimeChar, false);
                updateBarCharByName(flowStatistics, flowExecutionChar, true);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private void updateBarCharByName(Map<String, Pair<Integer, Duration>> statistics, BarChart<String,Number> curBarChar, Boolean isCountBased) {
            curBarChar.layout();

            XYChart.Series series1 = new XYChart.Series();

            for(String name : statistics.keySet()){
                series1.getData().add(new XYChart.Data(name, isCountBased ? statistics.get(name).getKey() : statistics.get(name).getValue().toMillis()));
            }
            curBarChar.getData().clear();
            curBarChar.getData().add(series1);
    }

}
