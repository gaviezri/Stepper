package body.execution;

import javafx.scene.Node;
import javafx.util.Duration;
import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.step.api.enums.StepResult;

import java.util.*;
import java.util.stream.Collectors;

public class SingleStepExecutionTableData {
   private String Name;
   private StepResult Result = StepResult.NOT_EXECUTED;
   private Duration Duration;
   private List<String> Logs;
   private String SummaryLine;
   private List<Node> OutputsNodes = new LinkedList<>();
   private Set<String> OutputsThatWereTakenCareOf = new TreeSet<String>();

    public SingleStepExecutionTableData(String stepName, StepResult stepResult, javafx.util.Duration duration, List<String> logs,String summaryLine ,Map<String, Pair<DataDefinition, Object>> outputName2DefAndVal) {
        Name = stepName;
        Result = stepResult;
        Duration = duration;
        Logs = logs;
        SummaryLine = summaryLine;
        for (Map.Entry<String, Pair<DataDefinition, Object>> entry : outputName2DefAndVal.entrySet()) {
            OutputsThatWereTakenCareOf.add(entry.getKey());
            OutputsNodes.add(createOutputNode(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue()));
        }
    }

    public String getSummaryLine() {
        return SummaryLine;
    }
    public String getName() {
        return Name;
    }

    public StepResult getResult() {
        return Result;
    }

    public javafx.util.Duration getDuration() {
        return Duration;
    }


    public List<String> getLogs() {
        return Logs;
    }

    public List<String> getOutputsName() {
        return OutputsThatWereTakenCareOf.stream().collect(Collectors.toList());
    }

    public void setStepResult(StepResult stepResult) {
        Result = stepResult;
    }

    public void updateData(StepResult stepResult, javafx.util.Duration duration, List<String> logs,String summaryLine, Map<String, Pair<DataDefinition, Object>> output2DefinitionAndValue) {
        Result = stepResult;
        Duration = duration;
        Logs = logs;
        SummaryLine =  summaryLine;

        for (Map.Entry<String, Pair<DataDefinition, Object>> entry : output2DefinitionAndValue.entrySet()) {
            if (OutputsThatWereTakenCareOf.contains(entry.getKey())) {
                continue;
            }
            OutputsThatWereTakenCareOf.add(entry.getKey());
            OutputsNodes.add(createOutputNode(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue()));
        }
    }

    public Node createOutputNode(String name, DataDefinition dataDefinition, Object value) {
        return null;
    }
}
