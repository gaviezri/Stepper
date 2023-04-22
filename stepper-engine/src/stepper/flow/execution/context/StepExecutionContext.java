package stepper.flow.execution.context;

import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.StepResult;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType);
    boolean storeDataValue(String dataName, Object value);

    // some more utility methods:
    // allow step to store log lines
    // allow steps to declare their summary line

    public AbstractLogger getStepLogger(StepDefinition step);
    public void setStepResult(String name, StepResult stepResult);
    public StepResult getStepResult(String name);
    public void setStepDuration(String name, Number duration);
    public Number getStepDuration(String name);
    public void tick(String name);
    public void tock(String name);

}
