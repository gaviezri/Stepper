package stepper.flow.execution.context;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.exception.GivenValueTypeDontMatchException;
import stepper.exception.NoMatchingKeyWasFoundException;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.StepResult;

import java.util.Map;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType) throws NoMatchingKeyWasFoundException, GivenValueTypeDontMatchException;
    void storeDataValue(String dataName, Object data, DataDefinitionRegistry value);
    String getCurrentStepName();
    void setCurrentStepName(String currentStepName);
    AbstractLogger getStepLogger();
    void setStepResult(String name, StepResult stepResult);
//    StepResult getStepResult(String name);
//    void setStepDuration(String name, Number duration);
//    Number getStepDuration(String name);
    void tick();
    void tock();
    void setCurrentStepUsageDeclaration(StepUsageDeclaration currentStepUsageDeclaration);
    Map getExecutionData();

    Object getStepsManagers();
}
