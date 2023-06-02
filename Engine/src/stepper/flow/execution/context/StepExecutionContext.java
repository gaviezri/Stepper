package stepper.flow.execution.context;

import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.exception.GivenValueTypeDontMatchException;
import stepper.exception.NoMatchingKeyWasFoundException;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.enums.StepResult;
import stepper.step.manager.StepExecutionDataManager;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType) throws NoMatchingKeyWasFoundException, GivenValueTypeDontMatchException;
    void storeDataValue(String dataName, Object data, DataDefinition value);
    String getCurrentStepName();
    void setCurrentStepName(String currentStepName);
    AbstractLogger getStepLogger();
    void setStepResult(String name, StepResult stepResult);
    void tick();
    void tock();
    void setCurrentStepUsageDeclaration(StepUsageDeclaration currentStepUsageDeclaration);
    Map getExecutionData();

    Object getStepsManagers();

    Map<String, Object> getExecutionDataValues();

    List<String> getStepLogs(String stepName);

    Map<String, Pair<DataDefinition,Object>> getStepOutputs(String stepName);

    StepExecutionDataManager getCurrentStepManager();

    Duration getStepDuration(String finalStepName);

    String getStepSummaryLine(String finalStepName);
}
