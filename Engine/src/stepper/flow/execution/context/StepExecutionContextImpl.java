package stepper.flow.execution.context;

import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.exception.*;
import stepper.flow.definition.aliasing.manager.DataAliasingManager;
import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.flow.definition.mapping.MappingGraph;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.enums.StepResult;
import stepper.step.manager.StepExecutionDataManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StepExecutionContextImpl implements StepExecutionContext {
    StepUsageDeclaration currentStepUsageDeclaration;
    private String currentStepName;
    private DataAliasingManager dataAliasingManager;
    private Map<String, Object> ExecutionDataValues; // final name TO value
    private Map<String, stepper.dd.api.DataDefinition> ExecutionDataName2Definition = new HashMap<>();
    private Map<String, StepExecutionDataManager> step2Manager = new LinkedHashMap<>();
    private MappingGraph mappingGraph;
    public String getFinalDataName(String name) {
        return dataAliasingManager.getAliasDataName(currentStepName, name);
    }
    @Override
    public String getCurrentStepName() {
        return currentStepName;
    }

    @Override
    public void setCurrentStepName(String currentStepName) {
        this.currentStepName = currentStepName;
    }

    public StepExecutionContextImpl(FlowDefinition flowDefinition, Map<String, Object> inputFinalName2Value, Map<String, String> inputFinalName2Definition, MappingGraph mappingGraph) {

        List<StepUsageDeclaration> steps = flowDefinition.getFlowSteps();
        dataAliasingManager = flowDefinition.getDataAliasingManager();

        for (StepUsageDeclaration step : steps) {
            step2Manager.put(step.getFinalStepName(), new StepExecutionDataManager(step.getFinalStepName()));
        }
        ExecutionDataValues = inputFinalName2Value;

        for (Map.Entry<String, String> entry : inputFinalName2Definition.entrySet()) {
            String finalName = entry.getKey();
            String value = entry.getValue().equals("Integer") ? "Number" : entry.getValue();
            try {
                stepper.dd.api.DataDefinition dataDefinition = DataDefinitionRegistry.valueOf(value.toUpperCase());
                ExecutionDataName2Definition.put(finalName, dataDefinition);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(value);
            }
        }
        this.mappingGraph = mappingGraph;

    }

    @Override
    public void setStepResult(String name, StepResult stepResult) {
        // assuming that from the step we can get to its data manager
        StepExecutionDataManager theManager = step2Manager.get(name);
        theManager.setStepResult(stepResult);
    }

    @Override
    public AbstractLogger getStepLogger() {
        // assuming that from the step we can get to its data manager
        StepExecutionDataManager theManager = step2Manager.get(currentStepName);
        return theManager.getStepLogger();
    }

    @Override
    public void tick() {
        // assuming that from the step we can get to its data manager
        // begin timing
        StepExecutionDataManager theManager = step2Manager.get(currentStepName);
        theManager.startTimer();
    }

    public void tock() {
        // assuming that from the step we can get to its data manager
        // end timing
        StepExecutionDataManager theManager = step2Manager.get(currentStepName);
        theManager.stopTimer();
    }

    @Override
    public void setCurrentStepUsageDeclaration(StepUsageDeclaration currentStepUsageDeclaration) {
        this.currentStepUsageDeclaration = currentStepUsageDeclaration;
        setCurrentStepName(currentStepUsageDeclaration.getFinalStepName());
    }

    @Override
    public <T> T getDataValue(String dataName, Class<T> expectedDataType) throws NoMatchingKeyWasFoundException, GivenValueTypeDontMatchException {

        String finalDataName = currentStepUsageDeclaration.getResourceFinalName(dataName);
        finalDataName = mappingGraph.getResourceNameThatMappedTo(finalDataName);
        stepper.dd.api.DataDefinition theExpectedDataDefinition = ExecutionDataName2Definition.get(finalDataName);

        if (theExpectedDataDefinition == null) {

            throw new NoMatchingKeyWasFoundException("The key " + finalDataName + " cant be found!");
        } else if (expectedDataType.isEnum()) {
            Object aValue = ExecutionDataValues.get(finalDataName);
            return (T) Enum.valueOf((Class<Enum>) expectedDataType, aValue.toString());

        } else if (expectedDataType.isAssignableFrom(theExpectedDataDefinition.getType())) {
            Object aValue = ExecutionDataValues.get(finalDataName);
            return expectedDataType.cast(aValue);
        } else {
            throw new GivenValueTypeDontMatchException("Expected type " + expectedDataType + "but " +
                    theExpectedDataDefinition.getClass() + " was given.");
        }

    }

    @Override                                                   // used to be flowDefinitionRegistry
    public void storeDataValue(String dataName, Object value, DataDefinition dataDefinition)  {
        // use current step name?
        String finalDataName = currentStepUsageDeclaration.getResourceFinalName(dataName);
        ExecutionDataName2Definition.put(finalDataName,dataDefinition);
        ExecutionDataValues.put(finalDataName, value);
    }

    @Override
    public Map getExecutionData() {
        return ExecutionDataValues;
    }

    @Override
    public  Map<String, StepExecutionDataManager> getStepsManagers() {
        return step2Manager;
    }

    @Override
    public Map<String,Object> getExecutionDataValues() {
        return ExecutionDataValues;
    }
}
