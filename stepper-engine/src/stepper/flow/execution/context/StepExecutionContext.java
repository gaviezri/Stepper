package stepper.flow.execution.context;

import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.StepDefinition;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType);
    boolean storeDataValue(String dataName, Object value);

    // some more utility methods:
    // allow step to store log lines
    // allow steps to declare their summary line

    public AbstractLogger getStepLogger(StepDefinition step);

}
