package body.execution;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.relation.RelationData;
import stepper.step.api.enums.StepResult;

import java.util.*;
import java.util.stream.Collectors;

import static stepper.dd.impl.DataDefinitionRegistry.LIST;
import static stepper.dd.impl.DataDefinitionRegistry.STRING;

public class SingleStepExecutionTableData {
    public class TableEntry {
        private String key;
        private String value;

        public TableEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }
        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

    }
   private String Name;
   private StepResult Result = StepResult.NOT_EXECUTED;
   private Duration Duration;
   private List<String> Logs;
   private String SummaryLine;
    private List<VBox> OutputsNodes = new LinkedList<>();
   private List<String> OutputsThatWereTakenCareOf = new ArrayList<>();
   private List<String> TypeOfOutputsThatWereTakenCareOf  = new ArrayList<>();
   private List<String> outputNamesAndTypes = new ArrayList<>();

    public SingleStepExecutionTableData(String stepName, StepResult stepResult, javafx.util.Duration duration, List<String> logs,String summaryLine ,Map<String, Pair<DataDefinition, Object>> outputName2DefAndVal) {
        Name = stepName;
        Result = stepResult;
        Duration = duration;
        Logs = logs;
        SummaryLine = summaryLine;
        updateOutputs(outputName2DefAndVal);
    }

    private void updateOutputs(Map<String, Pair<DataDefinition, Object>> outputName2DefAndVal) {
        for (Map.Entry<String, Pair<DataDefinition, Object>> entry : outputName2DefAndVal.entrySet()) {
            if (OutputsThatWereTakenCareOf.contains(entry.getKey()))
                continue;
            OutputsThatWereTakenCareOf.add(entry.getKey());
            OutputsNodes.add(createOutputNode(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue()));
            TypeOfOutputsThatWereTakenCareOf.add(entry.getValue().getKey().toString());
            outputNamesAndTypes.add(OutputsThatWereTakenCareOf.get(OutputsThatWereTakenCareOf.size()-1) +
                    " -- (" + TypeOfOutputsThatWereTakenCareOf.get(TypeOfOutputsThatWereTakenCareOf.size()-1) + ")");
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
        return outputNamesAndTypes;
    }

    public VBox getOutputNode(int index) {
        VBox replica = new VBox(OutputsNodes.get(index));
        return replica;
    }

    public void setStepResult(StepResult stepResult) {
        Result = stepResult;
    }

    public void updateData(StepResult stepResult, javafx.util.Duration duration, List<String> logs,String summaryLine, Map<String, Pair<DataDefinition, Object>> output2DefinitionAndValue) {
        Result = stepResult;
        Duration = duration;
        Logs = logs;
        SummaryLine =  summaryLine;
        updateOutputs(output2DefinitionAndValue);
    }

    public VBox createOutputNode(String name, DataDefinition dataDefinition, Object value) {
        VBox backBone = new VBox();
        backBone.setSpacing(10);

        HBox nameAndType = new HBox();
        nameAndType.setSpacing(10);
        backBone.getChildren().add(nameAndType);
        Label nameLabel = new Label("Name: " + name);
        nameLabel.setWrapText(true);
        Label typeLabel = new Label("Type: " + dataDefinition.toString());
        typeLabel.setWrapText(true);

        nameAndType.getChildren().add(nameLabel);
        nameAndType.getChildren().add(typeLabel);

        Node outputNode = null;

        switch ((DataDefinitionRegistry)dataDefinition) {
            case STRING:
            case DOUBLE:
            case NUMBER:
            case ENUMERATION:
            case MAPPING:
                outputNode = new Label(String.format("%s", value));

                break;
            case LIST:
                outputNode = new TextArea(((List<?>) value).stream().map(Object::toString).collect(Collectors.joining("\n")));
                ((TextArea) outputNode).editableProperty().set(false);
                break;
            case RELATION:
                ObservableList<SingleStepExecutionTableData.TableEntry> dataList = FXCollections.observableArrayList();
               TableView<SingleStepExecutionTableData.TableEntry> relationNode = new TableView<>();
                RelationData relationData = ((RelationData) value);
                for (int i = 0; i < relationData.getColSize(); ++i){

                    TableColumn<SingleStepExecutionTableData.TableEntry, String> column = new TableColumn<>(relationData.getColumnsNames().get(i));
                    column.setCellValueFactory(new PropertyValueFactory<>("value"));
                    column.editableProperty().set(false);
                    relationNode.getColumns().add(column);
                }
                for (int i = 0; i< relationData.getRowSize(); ++i) {
                    for (int j = 0; j < relationData.getColSize(); ++j) {
                        dataList.add(new SingleStepExecutionTableData.TableEntry(relationData.getColumnsNames().get(j), relationData.getDataFromCell(i, j)));
                    }
                }
                relationNode.setItems(dataList);
                relationNode.setEditable(false);
                outputNode = relationNode;
                break;
        }
        backBone.getChildren().add(outputNode);
        return backBone;
    }
}
