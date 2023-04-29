package stepper.controller;

import javafx.util.Pair;
import stepper.dto.DTO;
import stepper.dto.flow.FlowDefinitionDTO;
import stepper.dto.flow.FlowNamesDTO;
import stepper.dto.flow.LoadDataDTO;
import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.archive.ExecutionArchive;
import stepper.flow.loader.LoadedFlowsLibrary;
import stepper.flow.execution.runner.FlowExecutor;
import stepper.flow.loader.FlowLoader;

import java.util.Map;

public class EngineController {

    ExecutionArchive executionArchive = new ExecutionArchive();
    FlowLoader flowLoader = new FlowLoader();
    LoadedFlowsLibrary flowLibrary = new LoadedFlowsLibrary();
    FlowExecutor flowExecutor = new FlowExecutor();


    public DTO readXML(String path) {
        try {
            flowLibrary.setLoadedflowDefinitions(flowLoader.loadFlowFromXML(path));
            flowExecutor.reset();
        }catch (Exception e){
            return new LoadDataDTO(path, "", false, e.getMessage());
        }
        return new LoadDataDTO(path, "Flow(s) loaded successfully!", true, "");
    }
    public DTO getFlowDefinitionData(Integer flowIdx){
        try {
            return new FlowDefinitionDTO(flowLibrary.getFlowDefinition(flowIdx));
        }catch (Exception e){
            return new LoadDataDTO("", "", false, e.getMessage());
        }
    }

    public FlowNamesDTO getFlowDefinitionsNames(){
        try {
            return new FlowNamesDTO(flowLibrary.getFlowDefinitionsNames());
        }catch (Exception e){
            return new FlowNamesDTO(null, false, "No Flows Loaded Yet!");
        }
    }

    public DTO executeFlow(Integer flowIdx, Pair<Map,Map> valName2valType){

        FlowDefinition flowToExecute = flowLibrary.getFlowDefinition(flowIdx);
        flowExecutor.setActiveFlow(flowToExecute);
        flowExecutor.setFlowFreeInputs(valName2valType);
        executionArchive.push(new FlowExecution(flowToExecute));
        flowExecutor.executeFlow(executionArchive.peek());
        return null;

    }

    public DTO saveSystemState(String path){
        return null;
    }

    public boolean isLoaded() {
        return flowLibrary.isLoaded();
    }
}
