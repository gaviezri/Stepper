package stepper.flow.loader;

import stepper.flow.definition.api.FlowDefinition;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoadedFlowsLibrary implements Serializable {
    private List<FlowDefinition> loadedflowDefinitions = null;

    public void addLoadedFlowDefinitions(List<FlowDefinition> newLoadedFlowDefinitions) {
        if (this.loadedflowDefinitions != null) {
            for (FlowDefinition flowDefinition : newLoadedFlowDefinitions) {
//              add only flows with new names
                if(this.loadedflowDefinitions.stream().
                        map(FlowDefinition::getName).
                        noneMatch(x->x.equals(flowDefinition.getName()))){
                    this.loadedflowDefinitions.add(flowDefinition);
                }
            }
        } else if (newLoadedFlowDefinitions.size() > 0) {
            this.loadedflowDefinitions = newLoadedFlowDefinitions;
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


    public List<String> getFlowDefinitionsNames(boolean readonly) {
        return loadedflowDefinitions.stream()
                .flatMap(x -> readonly && !x.isReadOnly() ? Stream.empty() : Stream.of(x))
                .map(FlowDefinition::getName)
                .collect(Collectors.toList());
    }

    public int getCountOfLoadedFlows(){
        return loadedflowDefinitions == null ? 0 : loadedflowDefinitions.size();
    }

    public boolean isLoaded() {
        if(loadedflowDefinitions == null)
            return false;

        return !loadedflowDefinitions.isEmpty();
    }
}
