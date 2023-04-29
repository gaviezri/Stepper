package stepper.flow.execution.context;

import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.exception.*;
import stepper.flow.definition.aliasing.manager.DataAliasingManager;
import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.flow.execution.FlowExecutionResult;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.StepResult;
import stepper.step.manager.StepExecutionDataManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepExecutionContextImpl implements StepExecutionContext {
    /**
     * this dic will maintain all (data_value_name-alias) for data values in a flow.
     * if no alias was given, the dic will hold data_value_name-data_value_name for the step.
     */
    //private Map<String,String>  dataValueName2Alias = new HashMap<>();
    private DataAliasingManager dataAliasingManager;
    /**
     * this dic will maintain all concrete data (input/output) of a flow execution.
     * each step can address this data as input if needed and update it for outputs.
     */
    private Map<String, Object> ExecutionDataValues = new HashMap<>();
    /**
     * this dic will maintain all definition (input/output) of a flow execution.
     * each step can address this data as input if needed and update it for outputs.
     */
    private Map<String, DataDefinition> ExecutionDataName2Definition = new HashMap<>();
    /**
     * this dic will maintain (step-that_step_Manger).
     * */
    private Map<String, StepExecutionDataManager> step2Manager = new HashMap<>();

    public String getFinalDataName(String name){
        return dataAliasingManager.getAliasDataName(currentStepName,name);
    }
    @Override
    public String getCurrentStepName() {
        return currentStepName;
    }

    @Override
    public void setCurrentStepName(String currentStepName) {
        this.currentStepName = currentStepName;
    }
//TODO: see if really needed...
    private String currentStepName;

    public StepExecutionContextImpl(FlowDefinition flowDefinition, Map<String,String> inputFinalName2StringValue) {

        List<StepUsageDeclaration> steps = flowDefinition.getFlowSteps();
        dataAliasingManager = flowDefinition.getDataAliasingManager();

        for (StepUsageDeclaration step : steps) {
            step2Manager.put(step.getFinalStepName(),new StepExecutionDataManager(step.getFinalStepName()));
        }

        for (Map.Entry<String, String> entry : inputFinalName2StringValue.entrySet()) {
            String[] inpName_Type = entry.getKey().split(":");
            String strValue = entry.getValue();
            Object value = DataDefinitionRegistry.valueOf(inpName_Type[1].toUpperCase()).getType().cast(strValue);

            ExecutionDataValues.put(inpName_Type[0], value);
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
    public <T> T getDataValue(String dataName, Class<T> expectedDataType) throws NoMatchingKeyWasFoundException, GivenValueTypeDontMatchException {
        String finalDataName = dataAliasingManager.getAliasDataName(currentStepName,dataName);
        finalDataName = finalDataName == null ? dataName : finalDataName;

        // assuming that from the data name we can get to its data definition
        DataDefinition theExpectedDataDefinition = this.ExecutionDataName2Definition.get(finalDataName);

        if (theExpectedDataDefinition == null) throw new NoMatchingKeyWasFoundException("The key " + finalDataName + "cant be found!");

        if (expectedDataType.isAssignableFrom(theExpectedDataDefinition.getType())) {
            Object aValue = ExecutionDataValues.get(finalDataName);

            return expectedDataType.cast(aValue);
        } else {
            throw new GivenValueTypeDontMatchException("Expected type " + expectedDataType + "but " +
                    theExpectedDataDefinition.getClass() + " was given.");
        }

    }
    @Override
    public boolean storeDataValue(String dataName,Object value ,DataDefinitionRegistry datadefinition) throws GivenValueTypeDontMatchException{
        // use current step name?
        String finalDataName = dataAliasingManager.getAliasDataName(currentStepName,dataName);
        finalDataName = finalDataName == null ? dataName : finalDataName;

        // assuming that from the data name we can get to its data definition
        DataDefinition theData = ExecutionDataName2Definition.get(finalDataName);

        // we have the DD type, so we can make sure that its from the same type
        if (theData.getType().isAssignableFrom(datadefinition.getType())) {
            ExecutionDataValues.put(finalDataName, value);
        }
        else {
            throw new GivenValueTypeDontMatchException("Expected type " + theData.getType() + "but " + value.getClass() + "was given.");
        }

        return true;
    }

    @Override
    public FlowExecutionResult getFlowExecutionResult() {
        FlowExecutionResult flowExecutionResult = FlowExecutionResult.SUCCESS;
        for (Map.Entry<String, StepExecutionDataManager> entry : step2Manager.entrySet()) {
            StepExecutionDataManager stepExecutionDataManager = entry.getValue();
            StepResult stepResult = stepExecutionDataManager.getStepResult();
            if (stepResult.equals(StepResult.FAILURE)) {
                flowExecutionResult = FlowExecutionResult.FAILURE;
                break;
            }
            if (stepResult.equals(StepResult.WARNING)) {
                flowExecutionResult = FlowExecutionResult.WARNING;
                break;
            }

        }
        return flowExecutionResult;
    }
}
