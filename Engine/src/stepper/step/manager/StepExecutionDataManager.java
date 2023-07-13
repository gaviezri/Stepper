package stepper.step.manager;

import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.flow.execution.logger.StepLogger;
import stepper.flow.execution.logger.LogLine;
import stepper.flow.execution.logger.step.StepExecutionLoggerImpl;
import stepper.manager.api.DataManager;
import stepper.step.api.enums.StepResult;
import stepper.step.execution.StepExecutionData;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StepExecutionDataManager implements DataManager, Serializable {
    // manager for each step in the flow
    // manages a step logger and the step's execution data i.e.
    private StepExecutionLoggerImpl stepLogger;
    private StepExecutionData stepExecutionData;

    public StepExecutionDataManager(String step){
        stepLogger = new StepExecutionLoggerImpl(step);
        stepExecutionData = new StepExecutionData();
    }

    public StepLogger getStepLogger() {
        return stepLogger;
    }

    public Duration getDuration() {
        return stepExecutionData.getDuration();
    }
    public void startTimer() {
        stepExecutionData.start();
    }
    public void stopTimer() {
        stepExecutionData.stop();
    }
    public StepResult getStepResult() {
        return stepExecutionData.getStepResult();
    }
    public void setStepResult(StepResult stepResult) {
        stepExecutionData.setStepResult(stepResult);
    }

    public String getStepSummaryLine() {
        try { return stepLogger.getSummaryLine().getLine(); }
        catch (Exception e) { return null; }
    }

    public List<Pair<String, String>> getLogs2TimeStamp() {
        List<Pair<String, String>> logs2TimeStamp = new ArrayList<>();
        for (LogLine logLine : stepLogger.getLogs()) {
            logs2TimeStamp.add(new Pair<>(logLine.getLine(), logLine.getTimeStamp().toString()));
        }
        return logs2TimeStamp;
    }


    public Map<String, Pair<DataDefinition,Object>> getStepOutputs() {
        return stepExecutionData.getStepOutputs();
    }

    public void addStepOutput(String finalDataName, Object value, DataDefinition dataDefinition) {
        stepExecutionData.addStepOutput(finalDataName, value, dataDefinition);
    }
}
