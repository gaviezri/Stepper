package body.execution;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.util.Pair;
import stepper.dd.api.AbstractDataDefinition;
import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.relation.Relation;
import stepper.step.api.enums.StepResult;

import java.util.*;
import java.util.stream.Collectors;

public class SingleStepExecutionTableData {
    public class TableEntry {
        private final Map<String, StringProperty> properties = FXCollections.observableHashMap();

        public TableEntry(Map<String,String> map){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                properties.put(entry.getKey(), new SimpleStringProperty(entry.getValue()));
            }
        }
        public StringProperty getValue(String key){
            return properties.get(key);
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

    public SingleStepExecutionTableData(String stepName, StepResult stepResult, javafx.util.Duration duration, List<String> logs,String summaryLine ,Map<String, Pair<AbstractDataDefinition, Object>> outputName2DefAndVal) {
        Name = stepName;
        Result = stepResult;
        Duration = duration;
        Logs = logs;
        SummaryLine = summaryLine;
        updateOutputs(outputName2DefAndVal);
    }

    private void updateOutputs(Map<String, Pair<AbstractDataDefinition, Object>> outputName2DefAndVal) {
        for (Map.Entry<String, Pair<AbstractDataDefinition, Object>> entry : outputName2DefAndVal.entrySet()) {
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

    public void updateData(StepResult stepResult, javafx.util.Duration duration, List<String> logs,String summaryLine, Map<String, Pair<AbstractDataDefinition, Object>> output2DefinitionAndValue) {
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
                ((Label) outputNode).setWrapText(true);
                break;
            case LIST:
                outputNode = new TextArea(((List<?>) value).stream().map(Object::toString).collect(Collectors.joining("\n")));
                ((TextArea) outputNode).editableProperty().set(false);
                break;
            case RELATION:
                Relation relationData = ((Relation) value);
                // get list of rows (maps)
                List<Map<String, String>> relationRows = relationData.getRows();
                // extract unique keys as row columns
                Set<String> columnNames = new LinkedHashSet<>();
                for (Map<String, String> relationRow : relationRows) {
                    columnNames.addAll(relationRow.keySet());
                }

                ObservableList<TableEntry> tableEntries = FXCollections.observableArrayList();
                for (Map<String, String> relationRow : relationRows) {
                    tableEntries.add(new TableEntry(relationRow));
                }
                TableView<TableEntry> relationTableView = new TableView<>(tableEntries);
                for (String columnName : columnNames) {
                    TableColumn<TableEntry, String> column = new TableColumn<>(columnName);
                    column.setCellValueFactory(cellData -> cellData.getValue().getValue(columnName));
                    relationTableView.getColumns().add(column);
                }
                AnchorPane anchorPane = new AnchorPane(relationTableView);
                AnchorPane.setTopAnchor(relationTableView, 0.0);
                AnchorPane.setBottomAnchor(relationTableView, 0.0);
                AnchorPane.setLeftAnchor(relationTableView, 0.0);
                AnchorPane.setRightAnchor(relationTableView, 0.0);

                relationTableView.setEditable(false);
                outputNode = anchorPane;
                break;
        }

        backBone.getChildren().add(outputNode);
        return backBone;
    }
}
