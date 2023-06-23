package stepper.flow.loader;

import stepper.flow.definition.api.FlowDefinition;

import java.io.Serializable;
import java.util.List;

public class LoadedFlowsLibrary implements Serializable {
    private List<FlowDefinition> loadedflowDefinitions = null;

    public void AddLoadedflowDefinitions(List<FlowDefinition> loadedflowDefinitions) {
        if (this.loadedflowDefinitions != null) {
            loadedflowDefinitions.addAll(loadedflowDefinitions);
        } else {
            this.loadedflowDefinitions = loadedflowDefinitions;
        }
    }
    public FlowDefinition getFlowDefinitionByIndex(Integer flowIdx){
        if (flowIdx < 0 || flowIdx >= loadedflowDefinitions.size()){
            return null;
        }
        return loadedflowDefinitions.get(flowIdx);
    }

    public FlowDefinition getFlowDefinitionByName(String name){
        return loadedflowDefinitions.stream().filter(x->x.getName().equals(name)).findFirst().get();
    }


    public List<String> getFlowDefinitionsNames() {
        return loadedflowDefinitions.stream()
                .map(FlowDefinition::getName)
                .collect(java.util.stream.Collectors.toList());
    }

    public int getCountOfLoadedFlows(){
        return loadedflowDefinitions.size();
    }

    public boolean isLoaded() {
        if(loadedflowDefinitions == null)
            return false;

        return !loadedflowDefinitions.isEmpty();
    }
}
