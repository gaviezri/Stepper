package stepper.flow.definition.api;

import stepper.dd.api.DataDefinition;
import stepper.step.StepDefinitionRegistry;
import stepper.step.api.enums.DataNecessity;

import java.util.List;

public interface FlowDefinition {
    String getName();

    String getDescription();

    List<StepUsageDeclaration> getFlowSteps();

    List<String> getFlowFormalOutputs();

    void setFlowFormalOutputs(List<String> flowFormalOutputs);

    void validateFlowStructure();

    public List<StepDefinitionRegistry> getStepDefinitionRegistries();

    void setMandatoryInputs();

    void addStep(String stepName);

    void addStepAlias(String finalname);

    void setDescription(String s);

    void addFlowLevelAlias(String stepFinalName, String sourceDataName, String sourceDataAlias);

    String getStepFinalName(String stepName, boolean fromAlias);

    String getStepOriginalName(String stepName);

    void addCustomMapping(String stepName, String dataName, String targetStepName, String targetDataName);

    String getResourceFinalName(String stepFinalName, String dataName);

    void addStepAliasThatCanSkipIfFail(String finalname);

    void createMapping();
    List<String> getStepOutputsFinalNames(String stepFinalName);
    List<String> getStepInputsFinalNames(String stepFinalName);

    StepUsageDeclaration getStepUsageDeclaration(String sourceStepName);
    
    DataDefinition getResourceDataDefinition(String stepFinalName, String dataOriginalName);


    List<String> getStepOutputsOriginalNames(String sourceFinalStepName);

    List<String> getStepInputsOriginalNames(String targetFinalStepName);

    DataNecessity getResourceDataNecessity(String FinalStepName, String dataOriginalName);
}
