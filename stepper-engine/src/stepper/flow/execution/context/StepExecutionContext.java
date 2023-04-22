package stepper.flow.execution.context;

import stepper.dd.api.AbstractDataDefinition;
import stepper.exception.GivenValueTypeDontMatchException;
import stepper.exception.NoMatchingKeyWasFoundException;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.StepResult;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType) throws NoMatchingKeyWasFoundException, GivenValueTypeDontMatchException;
    boolean storeDataValue(String dataName, AbstractDataDefinition value) throws GivenValueTypeDontMatchException;

    // some more utility methods:
    // allow step to store log lines
    // allow steps to declare their summary line

    public void setCurrentStepName(String currentStepName);

    public AbstractLogger getStepLogger(StepDefinition step);
    public void setStepResult(String name, StepResult stepResult);
    public StepResult getStepResult(String name);
    public void setStepDuration(String name, Number duration);
    public Number getStepDuration(String name);
    public void tick(String name);
    public void tock(String name);

}
