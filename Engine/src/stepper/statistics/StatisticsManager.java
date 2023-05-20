package stepper.statistics;

import javafx.util.Pair;
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.archive.ExecutionArchive;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class StatisticsManager {
    /**
     * This objects create statistics on all flows and steps that had occurred.
     * ONLY FLOWS & STEPS FROM THE LAST FILE LOADED!
     */
    ExecutionArchive executionArchive;
    // <Original Step Name , (Occurrences Counter, Sum Durations)>
    Map<String, Pair<Integer, Duration>> stepStatistics;
    // <Flow Name , (Occurrences Counter, Sum Durations)>
    Map<String, Pair<Integer, Duration>> flowStatistics;

    public StatisticsManager(ExecutionArchive executionArchive) {
        this.executionArchive = executionArchive;
        this.stepStatistics = new HashMap<>();
        this.flowStatistics = new HashMap<>();
    }

    public Map<String, Pair<Integer, Duration>> getStepStatistics() {
        return stepStatistics;
    }

    public Map<String, Pair<Integer, Duration>> getFlowStatistics() {
        return flowStatistics;
    }

    public void collectStatistics() {
        FlowExecution curFlowExecution;
        String curFlowName;
        String curOriginalStepName;
        Duration curDuration = null;

        for (int i = 0; i < this.executionArchive.getNumOfFlowExecutions(); ++i) {
            curFlowExecution = this.executionArchive.getFlowExecutionByIndex(i);

            curFlowName = curFlowExecution.getFlowDefinition().getName();
            curDuration = curFlowExecution.getDuration();
            addFlowToStatistics(curFlowName, curDuration);

            for (String finalStepName : curFlowExecution.getFinalStepsName()) {
                curDuration = curFlowExecution.getStepDurationByFinalName(finalStepName);
                if(curDuration!=null) {
                    curOriginalStepName = curFlowExecution.getFlowDefinition().getStepOriginalName(finalStepName);
                    addStepToStatisticsByOriginalName(curOriginalStepName, curDuration);
                }
            }
        }
    }

    private void addStepToStatisticsByOriginalName(String curOriginalStepName, Duration curDuration) {
        Integer curCounter;
        if (!stepStatistics.containsKey(curOriginalStepName)) {
            stepStatistics.put(curOriginalStepName, new Pair<>(1, curDuration));
        } else {
            curCounter = stepStatistics.get(curOriginalStepName).getKey() + 1;
            curDuration.plus(stepStatistics.get(curOriginalStepName).getValue());
            stepStatistics.put(curOriginalStepName, new Pair<>(curCounter, curDuration));
        }
    }

    private void addFlowToStatistics(String curFlowName, Duration curDuration) {
        Integer curCounter;
        if (!this.flowStatistics.containsKey(curFlowName)) {
            this.flowStatistics.put(curFlowName, new Pair<>(1, curDuration));
        } else {
            curCounter = this.flowStatistics.get(curFlowName).getKey() + 1;
            curDuration.plus(this.flowStatistics.get(curFlowName).getValue());
            this.flowStatistics.put(curFlowName, new Pair<>(curCounter, curDuration));
        }
    }
}
