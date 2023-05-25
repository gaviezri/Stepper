package stepper.flow.builder;

import stepper.dd.api.DataDefinition;
import stepper.flow.definition.api.FlowDefinition;

import java.util.List;
import java.util.Set;

public interface FlowBuilder {
    boolean doesThisFlowExist(String flowName);

    boolean isInputOfFlow(int flowInd, String dataName);

    boolean isOutputOfFlow(int flowInd, String dataName);

    void addTargetFlowToFlowsContinuation(int srcFlowInd, String targetFlowName);

    void addSrc2DataToFlowContinuationByTargetFlowsName(int srcFlowInd, String targetFlowName, String srcData, String targetData);

    List<String> getAllFlowsInputsFinalNamesByIndex(int flowInd);

    void reset();

    void addFlowsInitialInputValues(String inputName, Object initialValue, int flowInd, DataDefinition curDD);

    void instantiateFlowsAndSetNames(List<String> flowNames);
    void setFlowFormalOutputs(int flowidx, Set<String> flowFormalOutputs);
    void setFlowDescription(List<String> descriptions);
    public List<FlowDefinition> buildFlows();
    void addStepToFlow(int flow, String name, String finalname, boolean skipIfFail);
    void addFlowLevelAlias(int flowidx, String stepFinalName, String sourceDataName, String sourceDataAlias);
    String getStepOriginalName(int flowidx, String stepName);
    void addCustomMapping(int flowidx, String stepName, String dataName, String targetStepName, String targetDataName);
    String getResourceFinalName(int flowidx, String sourceStepFinalName, String sourceDataName);

    String getStepFinalName(int flowidx, String sourceStepName);

    DataDefinition getCorespondingDataDef(String inputName, int flowInd);

    Enum isValidEnumInputNameAndValue(String inputName, String inputValue);

    String getFlowNameByInd(int flowInd);
}
