package dto.execution.progress;

import javafx.util.Pair;
import dto.AbstractDTO;
import stepper.dd.api.DataDefinition;
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.data.collector.ExecutionDataCollector;
import stepper.step.api.enums.StepResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExecutedFlowDetailsDTO extends AbstractDTO {
    /* 1 */ final private String flowExecutionId;
    /* 2 */ final private String flowName;
    /* 3 */ final private String flowExecutionResult;
    /* 4 */ final private String executionTimeInMillis;
    /* 5.1 */ final private List<String> freeInputsFinalNames;
    /* 5.2 */ final private List<String> freeInputsTypes;
    /* 5.3 */ final private List<String> freeInputsContent ;
    /* 5.4 */ final private List<String> freeInputsNecessity;
    /* 6.1 */ final private List<String> outputsFinalNames;
    /* 6.2 */ final private List<String> outputsTypes;
    /* 6.3 */ final private Map outputsContent;
    /* 7.1 */ final private List<String> stepsNamesWithAlias;
    /* 7.2 */ final private Map<String, Duration> steps2DurationInMillis;
    /* 7.3 */ final private Map<String,StepResult> stepsResult;
    /* 7.4 */ final private Map<String,String> stepsSummaryLine;
    /* 7.5 */ final private List<List<Pair<String,String>>> stepsLogs2TimeStamp;
              final private boolean isExecutionInProgress;
              final private int currentStepIdx;
              final private String currentStepName;


    public ExecutedFlowDetailsDTO(ExecutionDataCollector dataCollector) {

        FlowExecution flowExecution = dataCollector.getFlowExecution();
        freeInputsContent = new ArrayList<>();
        Map<String,String> headers = flowExecution.getFlowHeader();
        isExecutionInProgress = dataCollector.isFlowExecutionInProgress();
        flowExecutionId = headers.get("ID");
        flowName = headers.get("Name");
        executionTimeInMillis = flowExecution.getDurationInMillis().toString();
        flowExecutionResult = String.valueOf(flowExecution.getFlowExecutionResult());
        freeInputsFinalNames = flowExecution.getFlowDefinition().getFreeInputsFinalNames();
        freeInputsTypes = flowExecution.getFlowDefinition().getFreeInputsTypes();
        for (String freeInputName : freeInputsFinalNames){
            try {
                freeInputsContent.add(flowExecution.getFreeInputContent(freeInputName));
            } catch (Exception e) {
                freeInputsContent.add("N/A");
            }
        }

        freeInputsNecessity = flowExecution.getFlowDefinition().getFreeInputsNecessity();
        outputsFinalNames = flowExecution.getFlowDefinition().getAllOutputsNames();
        outputsTypes = flowExecution.getFlowDefinition().getAllOutputsTypes();
        // TODO: The root function " FlowExecution.getAllOutputsContent() " was changed so that the return value will
        //  be Objects and not there string representatives
        outputsContent = dataCollector.getOutputsForAllSteps();

        stepsNamesWithAlias = flowExecution.getStepsNamesWithAlias();
        steps2DurationInMillis = flowExecution.getStepsDurationInMillis();

        stepsSummaryLine = flowExecution.getStepsSummaryLine();
        stepsLogs2TimeStamp = flowExecution.getStepsLogs2TimeStamp();
        stepsResult = dataCollector.getExecutedStepsStatus();
        currentStepIdx = dataCollector.getCurrentStepIdx();
        currentStepName = dataCollector.getCurrentStepName();
    }

    public String getFlowName() {
        return flowName;
    }

    public String getFlowExecutionResult() {
        return flowExecutionResult;
    }

    public String getExecutionTimeInMillis() {
        return executionTimeInMillis;
    }

    public List<String> getFreeInputsFinalNames() {
        return freeInputsFinalNames;
    }

    public List<String> getFreeInputsTypes() {
        return freeInputsTypes;
    }

    public List<String> getFreeInputsContent() {
        return freeInputsContent;
    }

    public List<String> getFreeInputsNecessity() {
        return freeInputsNecessity;
    }

    public List<String> getOutputsFinalNames() {
        return outputsFinalNames;
    }

    public List<String> getOutputsTypes() {
        return outputsTypes;
    }

    public List<String> getStepsNamesWithAlias() {
        return stepsNamesWithAlias;
    }

    public Map<String,Duration> getSteps2DurationInMillis() {
        return steps2DurationInMillis;
    }

    public Map<String,StepResult> getStepsResult() {
        return stepsResult;
    }

    public Map<String,String> getStepsSummaryLine() {
        return stepsSummaryLine;
    }

    public List<List<Pair<String, String>>> getStepsLogs2TimeStamp() {
        return stepsLogs2TimeStamp;
    }

    public String getFlowExecutionId() {
        return flowExecutionId;
    }

    public Boolean isExecutionInProgress() {
        return isExecutionInProgress;
    }

    public int getStepsCount(){
        return stepsNamesWithAlias.size();
    }

    public float getFlowExecutionProgress(){
        return (currentStepIdx + 1.0f) / getStepsCount();
    }

    public Map<String,List<String>> getAllStepsListOfLogs(){
        return null;
    }

    public String getCurrentStepName() {
        return currentStepName;
    }

    public Map<String, Map<String, Pair<DataDefinition, Object>>> getOutputsForAllSteps() {
        return outputsContent;
    }
}
