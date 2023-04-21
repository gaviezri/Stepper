package stepper.step.impl;

import stepper.flow.execution.context.StepExecutionContext;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.StepResult;

public class HelloWorldStep extends AbstractStepDefinition {

    public HelloWorldStep() {
        super("Hello World", true);

        // no inputs

        // no outputs
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        System.out.println("Hello world !");
        return StepResult.SUCCESS;
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return null;
    }
}
