package stepper.flow.execution.runner;

import javafx.util.Pair;
import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.FlowExecutionResult;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.context.StepExecutionContextImpl;
import stepper.flow.execution.last.executed.data.center.LastExecutedDataCenter;
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

    public FlowExecutionResult call () {
        UUID flowUUID = flowExecution.getUniqueId();
        System.out.println("Starting execution of flow " + LastExecutedDataCenter.setLastExecutedFlowName(activeFlow.getName(),flowUUID) + " [ID: " + flowUUID  + "] By Thread: " + Thread.currentThread().getName());
        List<StepUsageDeclaration> stepsList = flowExecution.getFlowDefinition().getFlowSteps();
        boolean breakFlowIfStepFails;

        LastExecutedDataCenter.setStepsCount(stepsList.size(),flowUUID);


        flowExecution.setFreeInputContent(context.getExecutionData());
        flowExecution.setInitialValuesContent(context.getExecutionData());
        flowExecution.setFinalStepName2stepsManagers((Map<String, StepExecutionDataManager>) context.getStepsManagers());
        // start actual execution
        flowExecution.tick();
        StepResult stepResult = StepResult.FAILURE;
        LastExecutedDataCenter.startFlow(flowUUID);
        for (int i = 0; i < stepsList.size(); i++) {
            StepUsageDeclaration currentStepUsageDeclaration = stepsList.get(i);
            setCurrentStepInDataCenter(flowUUID, i, currentStepUsageDeclaration);
            Boolean skipIfFail = currentStepUsageDeclaration.skipIfFail();
            String finalStepName = currentStepUsageDeclaration.getFinalStepName();
            System.out.println("Starting to execute step: " + finalStepName);
            context.setCurrentStepUsageDeclaration(currentStepUsageDeclaration);
            try {
                stepResult = currentStepUsageDeclaration.getStepDefinition().invoke(context);
            } catch (Exception e){
                e.printStackTrace();
            }
            setStepsOutputInDataCenter(flowUUID, finalStepName, stepResult);
            System.out.println("Done executing step: " + finalStepName + ". Result: " + stepResult);
            context.setStepResult(finalStepName, stepResult);
            updateExecutionResult(flowExecution, stepResult, skipIfFail);
            breakFlowIfStepFails = (stepResult == StepResult.FAILURE && !skipIfFail);
            if (breakFlowIfStepFails) {
                break;
            }
        }
        setFlowFinishedOnFlowExecution();
        setFlowFinishedOnDataCenter(flowUUID);
        presentEndOfExecutionSummary(flowExecution);
        return flowExecution.getFlowExecutionResult();
    }

    private void setStepsOutputInDataCenter(UUID flowUUID, String finalStepName, StepResult stepResult) {
        LastExecutedDataCenter.setStepLogs(finalStepName, context.getStepLogs(finalStepName), flowUUID);
        LastExecutedDataCenter.setStepOutputs(finalStepName, context.getStepOutputs(finalStepName), flowUUID);
        LastExecutedDataCenter.setStepResult(finalStepName, stepResult, flowUUID);
        LastExecutedDataCenter.setStepsDuration(finalStepName, context.getStepDuration(finalStepName), flowUUID);
        LastExecutedDataCenter.setStepSummaryLine(finalStepName, context.getStepSummaryLine(finalStepName), flowUUID);
    }

    private void setFlowFinishedOnFlowExecution() {
        flowExecution.tock();
        flowExecution.setExecutionOutputs(context.getExecutionDataValues());
    }

    private void setFlowFinishedOnDataCenter(UUID flowUUID) {
        LastExecutedDataCenter.setExecutionOutputs(context.getExecutionDataValues(), flowUUID);
        LastExecutedDataCenter.endFlow(flowUUID, flowExecution.getFlowExecutionResult());
    }

    private static void setCurrentStepInDataCenter(UUID flowUUID, int i, StepUsageDeclaration currentStepUsageDeclaration) {
        LastExecutedDataCenter.setCurrentStepIdx(i, flowUUID);
        LastExecutedDataCenter.setCurrentStepName(currentStepUsageDeclaration.getFinalStepName(), flowUUID);
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
