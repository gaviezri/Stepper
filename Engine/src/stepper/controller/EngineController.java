package stepper.controller;

import javafx.util.Pair;
import stepper.dto.execution.history.FlowsExecutionHistoryDTO;
import stepper.dto.flow.ExecutedFlowDetailsDTO;
import stepper.dto.flow.FlowDefinitionDTO;
import stepper.dto.flow.FlowNamesDTO;
import stepper.dto.flow.LoadDataDTO;
import stepper.dto.statistics.StatisticsDTO;
import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.archive.ExecutionArchive;
import stepper.flow.execution.last.executed.data.center.LastExecutedDataCenter;
import stepper.flow.execution.runner.FlowExecutorsManager;
import stepper.flow.loader.LoadedFlowsLibrary;
import stepper.flow.execution.runner.FlowExecutor;
import stepper.flow.loader.FlowLoader;
import stepper.statistics.StatisticsManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class EngineController implements Serializable {

    ExecutionArchive executionArchive = new ExecutionArchive();
    FlowLoader flowLoader = new FlowLoader();
    LoadedFlowsLibrary flowLibrary = new LoadedFlowsLibrary();
    FlowExecutorsManager flowsExecutorsManager = new FlowExecutorsManager();
    private StatisticsManager statisticsManager = new StatisticsManager(getArchive());
    private static EngineController instance = null;


    private EngineController() {
    }

    public Stack<FlowExecution> getFlowExecutionStack(){
        return executionArchive.getFlowExecutionStack();
    }
    public synchronized static EngineController getInstance() {
        if (instance == null) {
            instance = new EngineController();
        }
        return instance;
    }
    public LoadDataDTO readXML(String path) {
        try {
            flowLibrary.setLoadedflowDefinitions(flowLoader.loadFlowFromXML(path));
            flowsExecutorsManager.setWorkersCount(flowLoader.getWorkersCount());
            executionArchive.clear();
        }catch (Exception e){
            return new LoadDataDTO(path, "", false, e.getMessage());
        }
        return new LoadDataDTO(path, "Flow(s) loaded successfully!", true, "");
    }
    public FlowDefinitionDTO getFlowDefinitionData(Integer flowIdx){
        try {
            return new FlowDefinitionDTO(flowLibrary.getFlowDefinition(flowIdx));
        }catch (Exception e){
            return new FlowDefinitionDTO(e.getMessage());
        }
    }
    public List<FlowDefinitionDTO> getAllFlowDefinitionsData(){
        try {
            List<FlowDefinitionDTO> allDTOS = new ArrayList<>();
            for (int i = 0; i < flowLibrary.getCountOfLoadedFlows(); ++i){
                allDTOS.add(getFlowDefinitionData(i));
            }
            return allDTOS;
        }catch (Exception e){
            return null;
        }
    }

    public FlowNamesDTO getFlowDefinitionsNames(){
        try {
            return new FlowNamesDTO(flowLibrary.getFlowDefinitionsNames());
        }catch (Exception e){
            return new FlowNamesDTO("No Flows Loaded Yet!");
        }
    }

    public void executeFlow(Integer flowIdx, Pair<Map,Map> valName2valType){

        FlowDefinition flowToExecute = flowLibrary.getFlowDefinition(flowIdx);
        FlowExecutor flowExecutor = new FlowExecutor();
        flowExecutor.setActiveFlow(flowToExecute);
        flowExecutor.setFlowFreeInputs(valName2valType);

        executionArchive.push(new FlowExecution(flowToExecute));
        flowExecutor.setFlowExecution(executionArchive.peek());

        flowsExecutorsManager.executeFlow(flowExecutor);
    }

    public List<Map<String,String>> getExecutedFlowHeaders() {
        return executionArchive.getExecutedFlowHeaders();

    }

    public boolean isLoaded() {
        return flowLibrary.isLoaded();
    }


    public boolean executionOccurred() {
        return executionArchive.executionOccurred();
    }

    public ExecutedFlowDetailsDTO getExecutedFlowDetails(int flowIdx) {
        return new ExecutedFlowDetailsDTO(executionArchive.get(flowIdx));
    }

    public StatisticsDTO getCurrentLoadedFlowsStatisticsDetails(){
        statisticsManager.collectStatistics();
        return new StatisticsDTO(statisticsManager.getStepStatistics(),statisticsManager.getFlowStatistics());
    }

    public FlowsExecutionHistoryDTO getExecutedFlowsHistoryDetails(){
        return new FlowsExecutionHistoryDTO(this.executionArchive.getFlowExecutionStack());
    }


//    public void testPrintingTODELETE(FlowsExecutionHistoryDTO.SortFilter filter){
//        FlowsExecutionHistoryDTO test = getExecutedFlowsHistoryDetails();
//
//        test.sorteFlowExecutionDTOsBy(filter);
//        for (List<Object> row: test.getFlowExecutionHistoryDetailsRows()) {
//            for (Object data:row){
//                System.out.print(data + "|");
//            }
//            System.out.println();
//        }
//    }

    public ExecutionArchive getArchive() {
        return this.executionArchive;
    }


    public void prepareForSerialization() {
        this.flowsExecutorsManager.prepareForSerialization();
    }

    public void wakeUp() {
        this.flowsExecutorsManager.wakeUp();
    }

    public int getNumOfFlowsExecuted() {
        return executionArchive.getNumOfFlowExecutions();
    }

    public int getNumOfFlowsFinished() {
        return flowsExecutorsManager.getNumOfFlowsFinished();
    }
}
