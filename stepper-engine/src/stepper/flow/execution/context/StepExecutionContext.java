package stepper.flow.execution.context;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.exception.GivenValueTypeDontMatchException;
import stepper.exception.NoMatchingKeyWasFoundException;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.StepResult;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType) throws NoMatchingKeyWasFoundException, GivenValueTypeDontMatchException;
    void storeDataValue(String dataName, Object data, DataDefinitionRegistry value);

    // some more utility methods:
    // allow step to store log lines
    // allow steps to declare their summary line

    String getCurrentStepName();

    public void setCurrentStepName(String currentStepName);

    public AbstractLogger getStepLogger();
    public void setStepResult(String name, StepResult stepResult);
    public StepResult getStepResult(String name);
    public void setStepDuration(String name, Number duration);
    public Number getStepDuration(String name);
    void tick();
    public void tock();

    void setCurrentStepUsageDeclaration(StepUsageDeclaration currentStepUsageDeclaration);
}
