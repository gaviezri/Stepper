package stepper.flow.execution.context;

import stepper.dd.api.AbstractDataDefinition;
import stepper.dd.api.DataDefinition;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.StepResult;
import stepper.step.manager.StepExecutionDataManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepExecutionContextImpl implements StepExecutionContext {
    /**
     * this dic will maintain all (input/output_name-Data_definition) of a flow.
     * each step can address this data as input if needed and update it for outputs.
     */
    private Map<String, AbstractDataDefinition> dataValues = new HashMap<>();
    /**
     * this dic will maintain (step-that_step_Manger).
     * */
    private Map<String, StepExecutionDataManager> step2Manager = new HashMap<>();
    /**
     * this dic will maintain all step_name-alias for steps in a flow.
     * if no alias was given, the dic will hold step_name-step_name for the step.
     */
//    private Map<String,String>  stepName2Alias = new HashMap<>();
//    /**
//     * this dic will maintain all (data_value_name-alias) for data values in a flow.
//     * if no alias was given, the dic will hold data_value_name-data_value_name for the step.
//     */
    private Map<String,String>  dataValueName2Alias = new HashMap<>();

//    public String getFinalStepName(String name){
//        return stepName2Alias.get(name);
//    }

    public String getFinalDataName(String name){
        return dataValueName2Alias.get(name);
    }

    public StepExecutionContextImpl(List<StepUsageDeclaration> steps, Map<String,String>  dataValueName2Alias) {
        for (StepUsageDeclaration step : steps) {
            step2Manager.put(step.getFinalStepName(),
                    new StepExecutionDataManager(step.getFinalStepName())
            );
        }
        this.dataValueName2Alias = dataValueName2Alias;
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
        StepExecutionDataManager theManager = step2Manager.get(step.getStepName());
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
