package stepper.flow.execution.data.collector;
import dto.execution.progress.ExecutedFlowDetailsDTO;
import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.FlowExecutionResult;
import stepper.step.api.enums.StepResult;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.Duration;

public class ExecutionDataCollector {
    private String executedFlowName;
    private UUID executedFlowUUID;
    private String currentStepName;
    private Integer currentStepIdx;
    private Integer stepsCount;
    private Map<String, Object> executionDataValues;
    private Map<String, StepResult> stepResults;
    private Map<String, List<String>> step2ListOfLogs;
    private Map<String ,Map<String,Pair<DataDefinition,Object>>> step2MapOfOutputsNames2DataDefAndValue;
    private Map<String, Duration> step2Duration = new HashMap<>();
    private Map<String,String> step2SummaryLine = new HashMap<>();
    private FlowExecutionResult flowExecutionResult = FlowExecutionResult.NONE;
    private Boolean isFlowInProgress = true;
    private FlowExecution flowExecution;


    public void setStepLogs(String finalStepName, List<String> stepLogs) {
            step2ListOfLogs.put(finalStepName, stepLogs);
    }

    public void setStepSummaryLine(String finalStepName, String stepSummaryLine) {
            step2SummaryLine.put(finalStepName, stepSummaryLine);
    }
    public  void setStepOutputs(String finalStepName, Map<String, Pair<DataDefinition, Object>> stepOutputs) {
            step2MapOfOutputsNames2DataDefAndValue.put(finalStepName, stepOutputs);
    }

    public String getExecutedFlowName() {
        return executedFlowName;
    }


    public void setCurrentStepName(String currentStepName) {
            this.currentStepName = currentStepName;
    }

    public void setCurrentStepIdx(Integer currentStepIdx) {
            this.currentStepIdx = currentStepIdx;
    }

    public void setStepsCount(Integer stepsCount) {
            this.stepsCount = stepsCount;
    }

    public void setStepsDuration(String stepName,Duration duration) {
            step2Duration.put(stepName, duration);
    }

    public void setExecutionOutputs(Map<String, Object> executionData) {
        executionDataValues = executionData;
    }

    public void setStepResult(String finalStepName, StepResult stepResult) {
            stepResults.put(finalStepName, stepResult);
    }

    public  Map<String,StepResult> getExecutedStepsStatus() {
        return stepResults;
    }

    public Map<String,List<String>> getAllStepsListOfLogs() {
        return step2ListOfLogs;
    }

    public Map<String,Map<String,Pair<DataDefinition,Object>>> getOutputsForAllSteps() {
        return step2MapOfOutputsNames2DataDefAndValue;
    }

    public String getCurrentStepName() {
        return currentStepName;
    }

    public  Integer getCurrentStepIdx() {
        return currentStepIdx;
    }

    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult;
    }

    public Map<String,Duration> getAllStepsDuration() {
        return step2Duration;
    }

    public Integer getStepsCount() {
        return stepsCount;
    }

    public void endFlow(FlowExecutionResult flowExecutionResult) {
          isFlowInProgress = false;
          this.flowExecutionResult = flowExecutionResult;
    }


    public boolean isFlowExecutionInProgress()
    {
        return isFlowInProgress;
    }

    public Map<String, String> getAllSummaryLines() {
        return step2SummaryLine;
    }

    public Map getLastFlowOutputs() {
        return executionDataValues;
    }

    public ExecutedFlowDetailsDTO getExecutionProgressDTO() {
        return null;
        //return new ExecutionProgressDTO();
    }

    public String setFlowUUID(UUID uniqueId) {
        this.executedFlowUUID = uniqueId;
        return executedFlowUUID.toString();
    }

    public void setFlowExecution(FlowExecution flowExecution) {
        this.flowExecution = flowExecution;

    }
    public FlowExecution getFlowExecution() {
        return flowExecution;
    }
}
