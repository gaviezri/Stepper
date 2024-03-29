package stepper.controller;

import dto.execution.progress.ExecutedFlowDetailsDTO;
import javafx.util.Pair;
import dto.execution.history.FlowsExecutionHistoryDTO;
import dto.flow.FlowDefinitionDTO;
import dto.flow.FlowNamesDTO;
import dto.flow.LoadDataDTO;
import dto.statistics.StatisticsDTO;
import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.archive.ExecutionArchive;
import stepper.flow.execution.runner.FlowExecutor;
import stepper.flow.execution.runner.FlowExecutorsManager;
import stepper.flow.loader.FlowLoader;
import stepper.flow.loader.LoadedFlowsLibrary;
import stepper.statistics.StatisticsManager;

import java.io.InputStream;
import java.io.Serializable;
import java.util.*;


public class EngineController implements Serializable {

    private final ExecutionArchive executionArchive = new ExecutionArchive();
    private final FlowLoader flowLoader = new FlowLoader();
    private final LoadedFlowsLibrary flowLibrary = new LoadedFlowsLibrary();
    private final FlowExecutorsManager flowsExecutorsManager = new FlowExecutorsManager();
    private final StatisticsManager statisticsManager = new StatisticsManager(getArchive());
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
            flowLibrary.addLoadedFlowDefinitions(flowLoader.loadFlowFromXML(path));
            flowsExecutorsManager.setWorkersCount(flowLoader.getWorkersCount());
            executionArchive.clear();
        }catch (Exception e){
            return new LoadDataDTO("", false, e.getMessage());
        }
        return new LoadDataDTO("Flow(s) loaded successfully!", true, "");
    }

    public LoadDataDTO readXML(InputStream XMLFileStream){
    /**
     *    This function is for the servlet
     * -> works with a stream of the file and not a path to file
     * */
        try{
            List<FlowDefinition> newlyLoadedFlows = flowLoader.loadFlowFromXML(XMLFileStream);
            flowLibrary.addLoadedFlowDefinitions(newlyLoadedFlows);
            flowsExecutorsManager.setWorkersCount(flowLoader.getWorkersCount());
        }catch (Exception e){
            return new LoadDataDTO("", false, e.getMessage());
        }
        return new LoadDataDTO("Flow(s) loaded successfully!", true, "");
    }

    public FlowDefinitionDTO getFlowDefinitionDataByIndex(Integer flowIdx){
        try {
            return new FlowDefinitionDTO(flowLibrary.getFlowDefinitionByIndex(flowIdx));
        }catch (Exception e){
            return new FlowDefinitionDTO(e.getMessage());
        }
    }

    public FlowDefinitionDTO getFlowDefinitionDtoByName(String name){
        try {
            return new FlowDefinitionDTO(flowLibrary.getFlowDefinitionByName(name));
        }catch (Exception e){
            return new FlowDefinitionDTO(e.getMessage());
        }
    }


    public List<FlowDefinitionDTO> getAllFlowDefinitionsData(){
        try {
            List<FlowDefinitionDTO> allDTOS = new ArrayList<>();
            for (int i = 0; i < flowLibrary.getCountOfLoadedFlows(); ++i){
                allDTOS.add(getFlowDefinitionDataByIndex(i));
            }
            return allDTOS;
        }catch (Exception e){
            return null;
        }
    }

    public FlowNamesDTO getFlowDefinitionsNames(boolean readonly){
        try {
            return new FlowNamesDTO(flowLibrary.getFlowDefinitionsNames(readonly));
        }catch (Exception e){
            return new FlowNamesDTO("No Flows Loaded Yet!");
        }
    }
    
    
    public UUID executeFlow(String flowName, Pair<Map,Map> valName2valType){
        FlowDefinition flowToExecute = flowLibrary.getFlowDefinitionByName(flowName);
        FlowExecutor flowExecutor = createFlowExecutor(valName2valType, flowToExecute);
        return flowsExecutorsManager.executeFlow(flowExecutor);
    }
    
    public UUID executeFlow(Integer flowIdx, Pair<Map,Map> valName2valType){
        FlowDefinition flowToExecute = flowLibrary.getFlowDefinitionByIndex(flowIdx);
        FlowExecutor flowExecutor = createFlowExecutor(valName2valType, flowToExecute);
        return flowsExecutorsManager.executeFlow(flowExecutor);
    }

    private FlowExecutor createFlowExecutor(Pair<Map, Map> valName2valType, FlowDefinition flowToExecute) {
        FlowExecutor flowExecutor = new FlowExecutor();
        flowExecutor.setActiveFlow(flowToExecute);
        flowExecutor.setFlowFreeInputs(valName2valType);
        executionArchive.push(new FlowExecution(flowToExecute));
        flowExecutor.setFlowExecution(executionArchive.peek());
        return flowExecutor;
    }

    public List<Map<String,String>> getExecutedFlowHeaders() {
        return executionArchive.getExecutedFlowHeaders();

    }

    public boolean isLoaded() {
        return flowLibrary.isLoaded();
    }


//    public ExecutedFlowDetailsDTO getExecutedFlowDetails(int flowIdx) {
//        return new ExecutedFlowDetailsDTO(executionArchive.get(flowIdx));
//    }

    public StatisticsDTO getCurrentLoadedFlowsStatisticsDetails(){
        statisticsManager.collectStatistics();
        return new StatisticsDTO(statisticsManager.getStepStatistics(),statisticsManager.getFlowStatistics());
    }

    public FlowsExecutionHistoryDTO getExecutedFlowsHistoryDetails(){
        return new FlowsExecutionHistoryDTO(this.executionArchive.getFlowExecutionStack());
    }

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

    public void stop() {
        flowsExecutorsManager.stop();

    }

    public ExecutedFlowDetailsDTO getExecutedFlowDetailsByUUID(UUID flowUUID) {
        return flowsExecutorsManager.getExecutedFlowDetailsByUUID(flowUUID);
    }
}
