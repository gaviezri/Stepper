package stepper.flow.execution;

import javafx.util.Pair;
import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.step.api.enums.StepResult;
import stepper.step.manager.StepExecutionDataManager;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FlowExecution implements Serializable {
    private final UUID uniqueId;
    private final FlowDefinition flowDefinition;
    private FlowExecutionResult flowExecutionResult = FlowExecutionResult.SUCCESS;
    private String formattedStartTime;
    private Instant startTimeInstant;
    private Instant endTimeInstant;
    private Duration duration;
    private Map<String,Object> freeInputContent = new HashMap<>();
    private Map<String, Object> executionOutputs = new HashMap<String, Object>();

    Map<String, StepExecutionDataManager> finalStepName2stepsManagers;


    public String getName(){
        return flowDefinition.getName();
    }
    public Set<String> getFinalStepsName(){
        return finalStepName2stepsManagers.keySet();
    }

    public Duration getStepDurationByFinalName(String finalName){
        return finalStepName2stepsManagers.get(finalName).getDuration();
    }
    // lots more data that needed to be stored while flow is being executed...

    public FlowExecution(FlowDefinition flowDefinition) {
        this.uniqueId = UUID.randomUUID();
        this.flowDefinition = flowDefinition;
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        this.formattedStartTime = time.format(formatter);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void tick(){
        startTimeInstant = Instant.now();
    }
    public void tock(){
        endTimeInstant = Instant.now();
        duration = Duration.between(startTimeInstant, endTimeInstant);
    }

    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult;
    }
    public void updateExecutionResult(StepResult stepExecResult, Boolean skipIfFail){
        // the flow initally is success, and will be changed to failure only if a step fails and skipIfFail is false
        // will change to warning if a step fails and skipIfFail is true or step is warning
        // once changed to warning, cannot be changed back to success
        // once changed to failure, cannot be changed back to warning or success
        if (stepExecResult == StepResult.FAILURE && !skipIfFail){
            flowExecutionResult = FlowExecutionResult.FAILURE;
        }
        else if (stepExecResult == StepResult.FAILURE && skipIfFail){
            flowExecutionResult = FlowExecutionResult.WARNING;
        }
        else if (stepExecResult == StepResult.WARNING && flowExecutionResult == FlowExecutionResult.SUCCESS){
            flowExecutionResult = FlowExecutionResult.WARNING;
        }
    }


    public Duration getDuration() {
        return duration;
    }
    
    public Long getDurationInMillis() {
        return duration.toMillis();

    }

    public String getFormattedStartTime() {
        return formattedStartTime;
    }

    public Map<String, String> getFlowHeader() {
        Map<String,String> flowHeader = new HashMap<>();
        flowHeader.put("ID", uniqueId.toString());
        flowHeader.put("Name", flowDefinition.getName());
        flowHeader.put("Time", formattedStartTime);
        return flowHeader;
    }

    public String  getFreeInputContent(String key) {
        return freeInputContent.get(key).toString();
    }

    public void setFreeInputContent(Map<String,Object> freeInputContent) {
        this.freeInputContent = freeInputContent;
    }

    public List<String> getStepsNamesWithAlias() {
        return flowDefinition.getStepsNamesWithAlias();
    }

    public List<String> getStepsDurationInMillis() {
        List<String> stepsDurationInMillis = new ArrayList<>();
        for (Map.Entry<String, StepExecutionDataManager> entry : finalStepName2stepsManagers.entrySet()) {
            StepExecutionDataManager stepExecutionDataManager = entry.getValue();
            try {
                stepsDurationInMillis.add(String.valueOf(stepExecutionDataManager.getDuration().toMillis()));
            } catch (NullPointerException e) {
                stepsDurationInMillis.add("0");
            }
        }
        return stepsDurationInMillis;
    }

    public void setFinalStepName2stepsManagers(Map<String, StepExecutionDataManager> finalStepName2stepsManagers) {
        this.finalStepName2stepsManagers = finalStepName2stepsManagers;
    }

    public void setExecutionOutputs(Map<String, Object> executionOutputs) {
        this.executionOutputs = executionOutputs;
    }

    public List<String> getStepsResult() {
        List<String> stepsResult = new ArrayList<>();
        for (Map.Entry<String, StepExecutionDataManager> entry : finalStepName2stepsManagers.entrySet()) {
            StepExecutionDataManager stepExecutionDataManager = entry.getValue();
            try {
                stepsResult.add(stepExecutionDataManager.getStepResult().toString());
            } catch (NullPointerException e) {
                stepsResult.add("step did not run.");
            }
        }
        return stepsResult;
    }

    public List<String> getStepsSummaryLine() {
        List<String> stepsSummaryLine = new ArrayList<>();
        for (Map.Entry<String, StepExecutionDataManager> entry : finalStepName2stepsManagers.entrySet()) {
            StepExecutionDataManager stepExecutionDataManager = entry.getValue();
            try {
                stepsSummaryLine.add(stepExecutionDataManager.getStepSummaryLine());
            } catch (NullPointerException e) {
                stepsSummaryLine.add("step did not run.");
            }
        }
        return stepsSummaryLine;
    }

    public List<List<Pair<String, String>>> getStepsLogs2TimeStamp() {
        List<List<Pair<String, String>>> stepsLogs2TimeStamp = new ArrayList<>();
        for (Map.Entry<String, StepExecutionDataManager> entry : finalStepName2stepsManagers.entrySet()) {
            StepExecutionDataManager stepExecutionDataManager = entry.getValue();
            try {
                stepsLogs2TimeStamp.add(stepExecutionDataManager.getLogs2TimeStamp());
            } catch (NullPointerException e) {
                stepsLogs2TimeStamp.add(null);
            }
        }
        return stepsLogs2TimeStamp;
    }

    public Collection<Object> getAllOutputsContent() {
        return this.executionOutputs.values();
    }
    private Object getOutputContentByName(String outputName) {
        return executionOutputs.get(outputName);
    }

    public void setInitialValuesContent(Map<String, Object> executionData) {
        for(String inputName : this.flowDefinition.getInitialInputName2Value().keySet()){
            executionData.put(inputName, flowDefinition.getInitialInputName2Value().get(inputName));
        }
    }
}
