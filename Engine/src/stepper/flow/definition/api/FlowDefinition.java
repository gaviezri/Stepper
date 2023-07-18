package stepper.flow.definition.api;

import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.flow.definition.aliasing.manager.DataAliasingManager;
import stepper.flow.definition.continuation.Continuation;
import stepper.flow.definition.mapping.MappingGraph;
import stepper.step.api.enums.DataNecessity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FlowDefinition {

    String getName();

    String getDescription();

    List<StepUsageDeclaration> getFlowSteps();

    List<String> getFlowFormalOutputs();

    Map<String, Object> getInitialInputName2Value();

    Integer getContinuationsCount();

    Continuation getContinuation();

    boolean isOutputOfFlow(String dataName);

    boolean isInputOfFlow(String dataName);

    List<String> getAllInputsFinalNames();

    void addFlowsInitialInputValues(String inputName, Object initialValue, DataDefinition curDD);

    static Map<String,String> getStepInput2UserString() {
        return null;
    }

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

    Set<String> getFlowOutputsNames();

    List<String> getStepInputsOriginalNames(String targetFinalStepName);

    DataNecessity getResourceDataNecessity(String FinalStepName, String dataOriginalName);

    void setReadonly();
    Boolean isReadOnly();
    void setFreeInputs();
    List<String> getFreeInputsFinalNames();

    List<String> getFreeInputsUserString();

    List<String> getFreeInputsTypes();

    List<String> getFreeInputsNecessity();

    List<String> getAllOutputsNames();

    List<String> getAllOutputsTypes();

    List<String> getFinalStepNamesThatProducedTheOutputs();

    DataAliasingManager getDataAliasingManager();

    MappingGraph getMappingGraph();

    String doesThisFinalStepNameExists(String sourceStepName);

    List<Pair<String, List<String>>> getFreeInputs2StepsThatUseThem();

    List<String> getStepsNamesWithAlias();

    String getDataDefOriginalName(String inputName);

    StepUsageDeclaration getStepUsageDeclarationByFinalName(String sourceStepName);
}
