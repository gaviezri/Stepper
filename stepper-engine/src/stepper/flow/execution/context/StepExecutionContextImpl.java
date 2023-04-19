package stepper.flow.execution.context;

import stepper.dd.api.DataDefinition;
import stepper.flow.execution.logger.StepExecutionLogger;
import stepper.flow.execution.logger.StepExecutionLoggerImpl;

import java.util.HashMap;
import java.util.Map;

public class StepExecutionContextImpl implements StepExecutionContext {

    private final Map<String, Object> dataValues;
    private final StepExecutionLogger logger;

    public StepExecutionContextImpl() {
        dataValues = new HashMap<>();
        logger = new StepExecutionLoggerImpl();
    }

    @Override
    public StepExecutionLogger getLogger() {
        return logger;
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
