package stepper.controller;

import stepper.dto.DTO;
import stepper.dto.flow.FlowDefinitionDTO;
import stepper.dto.flow.FlowNamesDTO;
import stepper.dto.flow.LoadDataDTO;
import stepper.flow.execution.runner.FlowExecutor;
import stepper.flow.loader.FlowLoader;

import java.util.Map;

public class EngineController {
    //Stack<FlowExecution> flowExecutionStack = new Stack<>();
    FlowExecutor flowExecutor = new FlowExecutor();
    FlowLoader flowLoader = new FlowLoader();

    public DTO readXML(String path) {
        try {
            flowExecutor.setFlowDefinitions(flowLoader.loadFlowFromXML(path));
            flowExecutor.reset();
        }catch (Exception e){
            return new LoadDataDTO(path, "", false, e.getMessage());
        }
        return new LoadDataDTO(path, "Flow(s) loaded successfully!", true, "");
    }
    public DTO getFlowDefinition(Integer flowIdx){
        try {
            return new FlowDefinitionDTO(flowExecutor.getFlowDefinition(flowIdx));
        }catch (Exception e){
            return new LoadDataDTO("", "", false, e.getMessage());
        }
    }
    public FlowNamesDTO getFlowDefinitionsNames(){
        try {
            return new FlowNamesDTO(flowExecutor.getFlowDefinitionsNames());
        }catch (Exception e){
            return new FlowNamesDTO(null, false, e.getMessage());
        }
    }
    public DTO executeFlow(Integer flowIdx, Map<String,String> inputFinalName2StringValue){
//        flowExecutor.setActiveFlow(flowIdx);
//        flowExecutor.setFlowFreeInputs(inputFinalName2StringValue);
//        flowExecutor.executeFlow();
        return null;
    }


}
