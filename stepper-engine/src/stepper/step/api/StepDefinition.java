package stepper.step.api;

import stepper.dd.api.DataDefinition;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.util.List;

public interface StepDefinition {
    String getStepName();
    boolean isReadonly();
    List<DataDefinitionDeclaration> inputs();
    List<DataDefinitionDeclaration> outputs();
    StepResult invoke(StepExecutionContext context);
    StepResult validateInputs(StepExecutionContext context);
    DataDefinition getResourceDataDefinition(String dataOriginalName);
    DataNecessity getResourceNecessity(String dataOriginalName);

}
