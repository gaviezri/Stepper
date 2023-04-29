package stepper.flow.definition.api;

import stepper.step.StepDefinitionRegistry;
import stepper.step.api.StepDefinition;

public interface StepUsageDeclaration {
    String getFinalStepName();
    StepDefinition getStepDefinition();
    boolean skipIfFail();

    StepDefinitionRegistry getStepDefinitionRegistry();

    String getInput2FinalName(String inputName);
    String getOutput2FinalName(String outputName);

    void putResource2FinalName(String resourceName, String finalName);

    String getResourceFinalName(String dataName);

    Boolean containsResource(String dataName);
}
