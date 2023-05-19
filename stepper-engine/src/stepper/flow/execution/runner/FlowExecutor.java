package stepper.flow.execution.runner;

import javafx.util.Pair;
import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.FlowExecutionResult;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.context.StepExecutionContextImpl;
import stepper.step.api.enums.StepResult;
import stepper.step.manager.StepExecutionDataManager;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public class FlowExecutor implements Serializable, Callable<FlowExecutionResult> {
    // context will hold the flow-execution data in the process. will be reset between flows.
    StepExecutionContext context;
    FlowDefinition activeFlow;
    FlowExecution flowExecution;
    FlowExecutorsManager flowExecutorsManager;

    public void reset(){
        context = null;
        activeFlow = null;
    }


    public FlowExecutionResult call () {
        System.out.println("Starting execution of flow " + activeFlow.getName() + " [ID: " + flowExecution.getUniqueId()  + "] By Thread: " + Thread.currentThread().getName());
        List<StepUsageDeclaration> stepsList = flowExecution.getFlowDefinition().getFlowSteps();

        boolean breakFlowIfStepFails;
        flowExecution.setFreeInputContent(context.getExecutionData());
        flowExecution.setFinalStepName2stepsManagers((Map<String, StepExecutionDataManager>) context.getStepsManagers());
        // start actual execution
        flowExecution.tick();
        for (int i = 0; i < stepsList.size(); i++) {
            StepUsageDeclaration currentStepUsageDeclaration = stepsList.get(i);
            Boolean skipIfFail = currentStepUsageDeclaration.skipIfFail();
            String finalStepName = currentStepUsageDeclaration.getFinalStepName();
            context.setCurrentStepUsageDeclaration(currentStepUsageDeclaration);

            System.out.println("Starting to execute step: " + finalStepName);
            StepResult stepResult = currentStepUsageDeclaration.getStepDefinition().invoke(context);
            System.out.println("Done executing step: " + finalStepName + ". Result: " + stepResult);
            context.setStepResult(finalStepName, stepResult);
            updateExecutionResult(flowExecution, stepResult, skipIfFail);
            breakFlowIfStepFails = (stepResult == StepResult.FAILURE && !skipIfFail);
            if (breakFlowIfStepFails) {
                break;
            }
        }
        flowExecution.tock();
        flowExecution.setExecutionOutputs(context.getExecutionDataValues());
        presentEndOfExecutionSummary(flowExecution);
        return flowExecution.getFlowExecutionResult();
    }

    private void presentEndOfExecutionSummary(FlowExecution flowExecution) {
        System.out.println("End execution of flow \"" + flowExecution.getFlowDefinition().getName() + "\" [ID: " + flowExecution.getUniqueId() + "]. Status: " + flowExecution.getFlowExecutionResult());
        for (String formalOutputName : flowExecution.getFlowDefinition().getFlowFormalOutputs()){
            String value = null;
            try {
                value = context.getExecutionData().get(formalOutputName).toString();
                value = value == null ? "Not Created Due To Failure" : value;
            } catch (Exception e) {
                value = "Not Created Due To Failure";
            }
            finally {
                System.out.println("Output: " + formalOutputName + " = " + value);
            }
        }
    }

    public void updateExecutionResult(FlowExecution execution, StepResult stepresult ,Boolean skipIfFail){
        execution.updateExecutionResult(stepresult, skipIfFail);
    }
    public void setActiveFlow(FlowDefinition flowDef) {
        activeFlow = flowDef;
    }

    public void setFlowFreeInputs(Pair<Map,Map> valMap2typeMap) {
        context = new StepExecutionContextImpl(activeFlow, valMap2typeMap.getKey(), valMap2typeMap.getValue(), activeFlow.getMappingGraph());
    }

    public void setFlowExecution(FlowExecution flowExecution) {
        this.flowExecution = flowExecution;
    }

    public UUID getFlowUUID() {
        return flowExecution.getUniqueId();
    }
}
