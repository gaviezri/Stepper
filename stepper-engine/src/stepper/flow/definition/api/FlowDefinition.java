package stepper.flow.definition.api;

import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.flow.definition.aliasing.manager.DataAliasingManager;
import stepper.flow.definition.mapping.MappingGraph;
import stepper.step.api.DataDefinitionDeclaration;
import stepper.step.api.enums.DataNecessity;

import java.util.List;

public interface FlowDefinition {
    String getName();

    String getDescription();

    List<StepUsageDeclaration> getFlowSteps();

    List<String> getFlowFormalOutputs();

    void setFlowFormalOutputs(List<String> flowFormalOutputs);

    void validateFlowStructure();

    void setMandatoryInputs();

    void addFinalizedStep(String name, String finalName, boolean skipIfFail);

    void setDescription(String s);

    void addFlowLevelAlias(String stepFinalName, String sourceDataName, String sourceDataAlias);

    String getStepOriginalName(String stepName);

    void addCustomMapping(String stepName, String dataName, String targetStepName, String targetDataName);

    String getResourceFinalName(String stepFinalName, String dataName);

    void createMapping();
    List<String> getStepOutputsFinalNames(String stepFinalName);
    List<String> getStepInputsFinalNames(String stepFinalName);

    DataDefinition getResourceDataDefinition(String stepFinalName, String dataOriginalName);

    List<String> getStepOutputsOriginalNames(String sourceFinalStepName);

    List<String> getStepInputsOriginalNames(String targetFinalStepName);

    DataNecessity getResourceDataNecessity(String FinalStepName, String dataOriginalName);

    void setAccessibility();
    Boolean getAccessibility();
    void setFreeInputs();
    List<String> getFreeInputsNames();

    List<String> getFreeInputsTypes();

    List<Pair<String, List<String>>> getFreeInputs2StepsThatUseThem();

    List<String> getFreeInputsNecessity();

    List<String> getAllOutputsNames();

    List<String> getAllOutputsTypes();

    List<String> getFinalStepNamesThatProducedTheOutputs();

    DataAliasingManager getDataAliasingManager();

    MappingGraph getMappingGraph();

    String getStepFinalName(String sourceStepName);

}
