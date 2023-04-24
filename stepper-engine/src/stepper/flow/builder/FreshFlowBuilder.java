package stepper.flow.builder;

import javafx.util.Pair;
import org.w3c.dom.NodeList;
import stepper.flow.definition.api.FlowDefinition;

import java.util.*;

public class FreshFlowBuilder implements FlowBuilder{
    private List<FlowDefinition> flowDefinitions = new ArrayList<>();
    private Map<String,Set<String>> flow2FormalOutputs = new TreeMap<>();
    private List<String> flowDescriptions = new ArrayList<>();
    private List<String> flowNames = new ArrayList<>();
    private Map<String,List<String>> flow2Steps = new HashMap<>();
    private List<Pair<String,String>> FlowColonStep2stepAlias = new ArrayList<>();
    public FreshFlowBuilder() {

    }



    public List<FlowDefinition> buildFlows() {
        // do some logic
        reset();
        return null;
    }
    public void reset() {
        //TODO: reset all fields
        flowDefinitions.clear();
        flowNames.clear();
        flow2Steps.clear();
    }

    public void addStep(NodeList stepNodeList) {
        // do some logic
    }
    @Override
    public void setFlowsNames(List<String> flowNames) {
        this.flowNames = flowNames;
    }
    @Override
    public void addStepAlias(String flow, String Step, String name) {
        String flowColonStep = flow + ":" + Step;
        FlowColonStep2stepAlias.add(new Pair<>(flowColonStep,name));
    }
    @Override
    public void addStepToFlow(String flowName, String stepName) {
        if (!flow2Steps.containsKey(flowName)) {
            flow2Steps.put(flowName, new ArrayList<>());
        }
        flow2Steps.get(flowName).add(stepName);
    }

    @Override
    public void setFlowFreeInputs(Set<String> flowFreeInputs) {

    }

    @Override
    public void setFlowFormalOutputs(String flowName, Set<String> flowFormalOutputs) {


    }

    @Override
    public List<String> getFlowSteps(String flowName) {
        return flow2Steps.get(flowName);
    }

    @Override
    public List<Pair<String, String>> getFlowSpecificStep2AliasByOrder(String flowName) {
        // return a list of mapping data <Step, Alias>
        List<Pair<String, String>> allStep2Aliases = new ArrayList<>();
        // get all flow,steps pairs that corresponds to given flow
        for (Pair flowColonStep2alias : FlowColonStep2stepAlias) {
            String[] flowColonStep = ((String)flowColonStep2alias.getKey()).split(":");
            if (flowColonStep[0].equals(flowName)) {
                allStep2Aliases.add(new Pair<>(flowColonStep[1], ((String) flowColonStep2alias.getValue())));
            }
        }
        return allStep2Aliases;
    }

    @Override
    public void setFlowDescription(String description) {
        flowDescriptions.add(description);
    }

}
