package stepper.flow.execution.runner;

import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.context.StepExecutionContextImpl;
import stepper.step.api.enums.StepResult;

import java.util.List;
import java.util.UUID;

public class FlowExecutor {
    List<StepExecutionContext> contexts;
    List<FlowDefinition> flowDefinitions;
    List<UUID> flowExecutionIds;





    public void executeFlow(FlowExecution flowExecution) {

        List<StepUsageDeclaration> stepsList = flowExecution.getFlowDefinition().getFlowSteps();
        System.out.println("Starting execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]");

        // To instantiate the context, we need to know the final step name of each step in the flow
        StepExecutionContext context = new StepExecutionContextImpl(flowExecution.getFlowSteps(),null);

        // populate context with all free inputs (mandatory & optional) that were given from the user
        // (typically stored on top of the flow execution object)
        boolean breakFlowIfStepFails = false;
        // start actual execution
        for (int i = 0; i < stepsList.size(); i++) {
            StepUsageDeclaration currentStepUsageDeclaration = stepsList.get(i);
            String finalStepName = currentStepUsageDeclaration.getFinalStepName();
            context.setCurrentStepName(finalStepName);

            System.out.println("Starting to execute step: " + finalStepName);
            StepResult stepResult = currentStepUsageDeclaration.getStepDefinition().invoke(context,finalStepName);
            System.out.println("Done executing step: " + finalStepName + ". Result: " + stepResult);
            context.setStepResult(finalStepName, stepResult);

            breakFlowIfStepFails = (stepResult == StepResult.FAILURE && !currentStepUsageDeclaration.skipIfFail());
            if (breakFlowIfStepFails) {
                break;
            }
        }



        System.out.println("End execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]. Status: " + flowExecution.getFlowExecutionResult());
    }
}
