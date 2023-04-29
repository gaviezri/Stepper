package stepper.flow.loader;

import stepper.flow.definition.api.FlowDefinition;

import java.util.List;

public class LoadedFlowsLibrary {
    private List<FlowDefinition> loadedflowDefinitions;

    public void setLoadedflowDefinitions(List<FlowDefinition> loadedflowDefinitions) {
        this.loadedflowDefinitions = loadedflowDefinitions;
    }
    public FlowDefinition getFlowDefinition(Integer flowIdx){
        if (flowIdx < 0 || flowIdx >= loadedflowDefinitions.size()){
            return null;
        }
        return loadedflowDefinitions.get(flowIdx);
    }


    public List<String> getFlowDefinitionsNames() {
        return loadedflowDefinitions.stream()
                .map(FlowDefinition::getName)
                .collect(java.util.stream.Collectors.toList());
    }
}
