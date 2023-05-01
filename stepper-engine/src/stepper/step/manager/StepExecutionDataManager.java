package stepper.step.manager;

import javafx.util.Pair;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.flow.execution.logger.LogLine;
import stepper.flow.execution.logger.step.StepExecutionLoggerImpl;
import stepper.manager.api.DataManager;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.StepResult;
import stepper.step.execution.StepExecutionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StepExecutionDataManager implements DataManager {
    // manager for each step in the flow
    // manages a step logger and the step's execution data i.e.
    private AbstractLogger stepLogger;
    private StepExecutionData stepExecutionData;

    public StepExecutionDataManager(String step){
        stepLogger = new StepExecutionLoggerImpl(step);
        stepExecutionData = new StepExecutionData();
    }

    public AbstractLogger getStepLogger() {
        return stepLogger;
    }

    public Number getDuration() {
        return stepExecutionData.getDuration();
    }
    public void setDuration(Number duration) {
        stepExecutionData.setDuration(duration);
    }
    public StepResult getStepResult() {
        return stepExecutionData.getStepResult();
    }
    public void setStepResult(StepResult stepResult) {
        stepExecutionData.setStepResult(stepResult);
    }

    public String getStepSummaryLine() {
        return stepLogger.getSummaryLine().getLine();
    }

    public List<Pair<String, String>> getLogs2TimeStamp() {
        List<Pair<String, String>> logs2TimeStamp = new ArrayList<>();
        for (LogLine logLine : stepLogger.getLogs()) {
            logs2TimeStamp.add(new Pair<>(logLine.getLine(), logLine.getTimeStamp().toString()));
        }
        return logs2TimeStamp;
    }

}
