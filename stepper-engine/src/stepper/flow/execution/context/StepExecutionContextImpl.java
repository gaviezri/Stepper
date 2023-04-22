package stepper.flow.execution.context;

import stepper.dd.api.DataDefinition;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.flow.definition.api.StepUsageDeclarationImpl;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.flow.execution.logger.flow.FlowExecutionLoggerImpl;
import stepper.flow.execution.logger.step.StepExecutionLoggerImpl;
import stepper.step.StepDefinitionRegistry;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.StepResult;
import stepper.step.manager.StepExecutionDataManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepExecutionContextImpl implements StepExecutionContext {

    private Map<String, Object> dataValues = new HashMap<>();
    private Map<String, StepExecutionDataManager> step2Manager = new HashMap<>();


    public StepExecutionContextImpl(List<StepUsageDeclaration> steps) {
        for (StepUsageDeclaration step : steps) {
            step2Manager.put(step.getFinalStepName(),
                    new StepExecutionDataManager(step.getFinalStepName())
            );
        }

    }
    @Override
    public void setStepResult(String name, StepResult stepResult) {
        // assuming that from the step we can get to its data manager
        StepExecutionDataManager theManager = step2Manager.get(name);
        theManager.setStepResult(stepResult);
    }
    @Override
    public StepResult getStepResult(String name) {
        // assuming that from the step we can get to its data manager
        StepExecutionDataManager theManager = step2Manager.get(name);
        return theManager.getStepResult();
    }
    @Override
    public void setStepDuration(String name, Number duration) {
        // assuming that from the step we can get to its data manager
        StepExecutionDataManager theManager = step2Manager.get(name);
        theManager.setDuration(duration);
    }

    @Override
    public Number getStepDuration(String name) {
        // assuming that from the step we can get to its data manager
        StepExecutionDataManager theManager = step2Manager.get(name);
        return theManager.getDuration();
    }
    @Override
    public AbstractLogger getStepLogger(StepDefinition step) {
        // assuming that from the step we can get to its data manager
        StepExecutionDataManager theManager = step2Manager.get(step.name());
        return theManager.getStepLogger();
    }

    @Override
    public void tick(String name) {
        // assuming that from the step we can get to its data manager
        // begin timing
        StepExecutionDataManager theManager = step2Manager.get(name);
        theManager.setDuration(System.currentTimeMillis() * -1);
    }
    public void tock(String name) {
        // assuming that from the step we can get to its data manager
        // end timing
        StepExecutionDataManager theManager = step2Manager.get(name);
        theManager.setDuration(System.currentTimeMillis() + theManager.getDuration().longValue());
    }


    @Override
    public <T> T getDataValue(String dataName, Class<T> expectedDataType) {
        // assuming that from the data name we can get to its data definition
        DataDefinition theExpectedDataDefinition = null;

        if (expectedDataType.isAssignableFrom(theExpectedDataDefinition.getType())) {
            Object aValue = dataValues.get(dataName);
            // what happens if it does not exist ?

            return expectedDataType.cast(aValue);
        } else {
            // error handling of some sort...
        }

        return null;
    }

    @Override
    public boolean storeDataValue(String dataName, Object value) {
        // assuming that from the data name we can get to its data definition
        DataDefinition theData = null;

        // we have the DD type so we can make sure that its from the same type
        if (theData.getType().isAssignableFrom(value.getClass())) {
            dataValues.put(dataName, value);
        } else {
            // error handling of some sort...
        }

        return false;
    }
}
