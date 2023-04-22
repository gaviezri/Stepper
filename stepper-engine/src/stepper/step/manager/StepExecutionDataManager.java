package stepper.step.manager;

import stepper.flow.execution.logger.AbstractLogger;
import stepper.flow.execution.logger.step.StepExecutionLoggerImpl;
import stepper.manager.api.DataManager;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.StepResult;
import stepper.step.execution.StepExecutionData;

import java.util.Optional;

public class StepExecutionDataManager implements DataManager {
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

}
