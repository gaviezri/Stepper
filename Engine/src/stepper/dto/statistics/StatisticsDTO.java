package stepper.dto.statistics;

import javafx.util.Pair;

import java.time.Duration;
import java.util.Map;

public class StatisticsDTO {
    // { Original Step Name : (Occurrences Counter, Sum Durations) }
    Map<String, Pair<Integer, Duration>> stepStatistics;
    // { Flow Name : (Occurrences Counter, Sum Durations) }
    Map<String, Pair<Integer, Duration>> flowStatistics;

    public StatisticsDTO(Map<String, Pair<Integer, Duration>> stepStatistics, Map<String, Pair<Integer, Duration>> flowStatistics) {
        this.stepStatistics = stepStatistics;
        this.flowStatistics = flowStatistics;
    }

    public Map<String, Pair<Integer, Duration>> getStepStatistics() {
        return stepStatistics;
    }

    public Map<String, Pair<Integer, Duration>> getFlowStatistics() {
        return flowStatistics;
    }
}
