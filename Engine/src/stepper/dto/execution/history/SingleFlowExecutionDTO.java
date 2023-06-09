package stepper.dto.execution.history;

import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.FlowExecutionResult;
import stepper.step.api.enums.StepResult;
import stepper.step.manager.StepExecutionDataManager;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class SingleFlowExecutionDTO {
    private String flowName;
    private String startTime; // need to be in format: {dd-mm-yyyy hh:mm:ss}
    private FlowExecutionResult flowExecutionResult;
    private  UUID uniqueId;
    private Map<String, Object> dataName2value;

    private Map<String, StepExecutionDataManager> finalStepName2stepsManagers = new LinkedHashMap<>();

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Map<String, Object> getDataName2value() {
        return dataName2value;
    }



    public SingleFlowExecutionDTO(FlowExecution flowExecution){
        this.flowName = flowExecution.getName();
        this.startTime = flowExecution.getFormattedStartTime();
        this.flowExecutionResult = flowExecution.getFlowExecutionResult();
        this.uniqueId = flowExecution.getUniqueId();
        this.dataName2value = flowExecution.getExecutionOutputs();
        this.finalStepName2stepsManagers = flowExecution.getFinalStepName2stepsManagers();
    }

    public String getFlowName() {
        return flowName;
    }

    public String getStartTime() {
        return startTime;
    }

    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult;
    }

    public List<Object> getFlowData(){
        List<Object> res = new ArrayList<>();

        res.add(flowName);
        res.add(startTime);
        res.add(flowExecutionResult);

        return res;
    }

    public List<String> getFinalStepsName(){
        return new ArrayList<>(finalStepName2stepsManagers.keySet());
    }

    public StepResult getStepExecutionResult(String stepName) {
        return finalStepName2stepsManagers.get(stepName).getStepResult();
    }

    public Duration getStepDuration(String stepName) {
        return finalStepName2stepsManagers.get(stepName).getDuration();
    }

    public List<String> getStepLogs(String stepName) {
        return finalStepName2stepsManagers.get(stepName).getLogs2TimeStamp().stream().map(x-> x.getKey() + " : " + x.getValue()).collect(Collectors.toList());
    }

    public String getStepSummaryLine(String stepName) {
        return finalStepName2stepsManagers.get(stepName).getStepSummaryLine();
    }

    public Map<String, Pair<DataDefinition, Object>> getStepOutputs(String stepName) {
        return finalStepName2stepsManagers.get(stepName).getStepOutputs();
    }
}
