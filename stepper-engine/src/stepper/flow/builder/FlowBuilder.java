package stepper.flow.builder;

import stepper.flow.definition.api.FlowDefinition;

import java.util.List;
import java.util.Set;

public interface FlowBuilder {
    void reset();
    void instantiateFlowsAndSetNames(List<String> flowNames);
    void addStepToFlow(int flowidx, String stepName);
    void setFlowFormalOutputs(int flowidx, Set<String> flowFormalOutputs);
    void setFlowDescription(List<String> descriptions);
    public List<FlowDefinition> buildFlows();
    void addStepAlias(int flow, String finalname, boolean skipIfFail);
    void addFlowLevelAlias(int flowidx, String stepFinalName, String sourceDataName, String sourceDataAlias);
    String getStepFinalName(int flowidx, String stepName, boolean fromAlias);
    String getStepOriginalName(int flowidx, String stepName);
    void addCustomMapping(int flowidx, String stepName, String dataName, String targetStepName, String targetDataName);
    String getResourceFinalName(int flowidx, String sourceStepFinalName, String sourceDataName);
}
