package stepper.flow.builder;

import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.definition.api.FlowDefinitionImpl;

import java.io.Serializable;
import java.util.*;

public class FlowBuilderImpl implements FlowBuilder, Serializable {
    private List<FlowDefinition> flowDefinitions = new ArrayList<>();

    public List<FlowDefinition> buildFlows() {
        // CHECK THAT:
        // reference from a later step to an earlier step - V
        // attempt to map between two data's that are not of the same type - V
        // there are mandatory inputs which are not user-friendly - V
        // flow output contains a data that doesn't exist in the flow's scope - V
        // numerous mandatory inputs with the same name from different types! - V
        // AND THEN OR SIMULTANEOUSLY
        // create list of step usage declaration for each flow in list
        for (FlowDefinition flowDefinition : flowDefinitions) {
            flowDefinition.createMapping();
            flowDefinition.validateFlowStructure();
            flowDefinition.setAccessibility();
            flowDefinition.setFreeInputs();
        }
        return flowDefinitions;
    }


    public void reset() {
        //TODO: reset all fields
        flowDefinitions.clear();
    }

    @Override
    public void instantiateFlowsAndSetNames(List<String> flowNames) {
        for (String flowName : flowNames) {
            flowDefinitions.add(new FlowDefinitionImpl(flowName));
        }
    }
    @Override
    public void addStepToFlow(int flowidx, String name, String finalname, boolean skipIfFail) {
        flowDefinitions.get(flowidx).addFinalizedStep(name, finalname, skipIfFail);
    }

    @Override
    public void setFlowFormalOutputs(int flowidx, Set<String> flowFormalOutputs) {
        List<String> formaloutputs = new ArrayList<>(flowFormalOutputs);
        flowDefinitions.get(flowidx).setFlowFormalOutputs(formaloutputs);
    }


    @Override
    public void setFlowDescription(List<String> description){
        for (int flowidx = 0 ; flowidx < description.size(); flowidx++) {
            flowDefinitions.get(flowidx).setDescription(description.get(flowidx));
        }

    }

    @Override
    public void addFlowLevelAlias(int flowidx, String stepFinalName, String sourceDataName, String sourceDataAlias) {
        flowDefinitions.get(flowidx).addFlowLevelAlias(stepFinalName, sourceDataName, sourceDataAlias);
    }

    @Override
    public String getStepOriginalName(int flowidx, String stepName){
        return flowDefinitions.get(flowidx).getStepOriginalName(stepName);
    }

    @Override
    public void addCustomMapping(int flowidx, String stepName, String dataName, String targetStepName, String targetDataName) {
        flowDefinitions.get(flowidx).addCustomMapping(stepName, dataName, targetStepName, targetDataName);
    }
    @Override
    public String getResourceFinalName(int flowidx, String StepFinalName, String DataName) {
        return flowDefinitions.get(flowidx).getResourceFinalName(StepFinalName, DataName);
    }

    @Override
    public String getStepFinalName(int flowidx, String sourceStepName) {
            return flowDefinitions.get(flowidx).getStepFinalName(sourceStepName);
    }

}
