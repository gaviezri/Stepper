package stepper.flow.builder;

import javafx.util.Pair;
import stepper.flow.definition.api.FlowDefinition;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FlowBuilder {
    void reset();
    void setFlowsNames(List<String> flowNames);
    

    void addStepToFlow(String flowIndex, String stepName);
    void setFlowFreeInputs(Set<String> flowFreeInputs);

    void setFlowFormalOutputs(String flowName, Set<String> flowFormalOutputs);

    List<Pair<String, String>> getFlowSpecificStep2AliasByOrder(String flowName);

    void setFlowDescription(String description);
    public List<FlowDefinition> buildFlows();

    void addStepAlias(String flow, String step, String name);
    
    List<String> getFlowSteps(String flowName);
}
