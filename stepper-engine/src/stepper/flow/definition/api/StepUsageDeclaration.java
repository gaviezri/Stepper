package stepper.flow.definition.api;

import stepper.step.StepDefinitionRegistry;
import stepper.step.api.StepDefinition;

public interface StepUsageDeclaration {
    String getFinalStepName();
    StepDefinition getStepDefinition();
    boolean skipIfFail();

    StepDefinitionRegistry getStepDefinitionRegistry();
}
