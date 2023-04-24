package stepper.flow.definition.api;

import stepper.step.StepDefinitionRegistry;
import stepper.step.api.DataDefinitionDeclaration;

import java.util.List;

public interface FlowDefinition {
    String getName();
    String getDescription();
    List<StepUsageDeclaration> getFlowSteps();
    List<String> getFlowFormalOutputs();
    void setFlowFormalOutputs(List<String> flowFormalOutputs);
    void validateFlowStructure();
    public List<StepDefinitionRegistry> getStepDefinitionRegistries();

    void addStep(String stepName);

    void addStepAlias(String finalname);

    void setDescription(String s);

    void addFlowLevelAlias(String stepFinalName, String sourceDataName, String sourceDataAlias);

    String getStepFinalName(String stepName, boolean fromAlias);

    String getStepOriginalName(String stepName);

    void addCustomMapping(String stepName, String dataName, String targetStepName, String targetDataName);

    String getResourceFinalName(String stepFinalName, String dataName);

    void addStepAliasThatCanSkipIfFail(String finalname);
}
