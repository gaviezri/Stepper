package GUI.body.execution;

import com.google.gson.internal.LinkedTreeMap;
import communication.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;
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
   private final String Name;
   private StepResult Result = StepResult.NOT_EXECUTED;
   private Duration Duration;
   private List<String> Logs;
   private String SummaryLine;
    private final List<VBox> OutputsNodes = new LinkedList<>();
   private final List<String> OutputsThatWereTakenCareOf = new ArrayList<>();
   private final List<String> TypeOfOutputsThatWereTakenCareOf  = new ArrayList<>();
   private final List<String> outputNamesAndTypes = new ArrayList<>();

    public SingleStepExecutionTableData(String stepName, StepResult stepResult, javafx.util.Duration duration, List<String> logs,String summaryLine ,Map<String, Pair<DataDefinition, Object>> outputName2DefAndVal) {
        Name = stepName;
        Result = stepResult;
        Duration = duration;
        Logs = logs;
        SummaryLine = summaryLine;
        updateOutputs(outputName2DefAndVal);
    }

    public SingleStepExecutionTableData(String stepName, StepResult stepResult, javafx.util.Duration duration, List<String> logs,String summaryLine, Map<String,Pair<String,Object>> outputName2DefAndVal, boolean a) {
        Name = stepName;
        Result = stepResult;
        Duration = duration;
        Logs = logs;
        SummaryLine = summaryLine;
        updateOutputs(outputName2DefAndVal, a);
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

    private void updateOutputs(Map<String,Pair<String,Object>> outputName2StrDefAndVal, boolean a){
        for (Map.Entry<String, Pair<String, Object>> entry : outputName2StrDefAndVal.entrySet()) {
            if (OutputsThatWereTakenCareOf.contains(entry.getKey()))
                continue;
            OutputsThatWereTakenCareOf.add(entry.getKey());
            OutputsNodes.add(createOutputNode(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue()));
            TypeOfOutputsThatWereTakenCareOf.add(entry.getValue().getKey());
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


    public void updateData(StepResult stepResult, javafx.util.Duration duration, List<String> logs,String summaryLine, Map<String, Pair<String, Object>> output2DefinitionAndValue, boolean a) {
        Result = stepResult;
        Duration = duration;
        Logs = logs;
        SummaryLine =  summaryLine;
        updateOutputs(output2DefinitionAndValue, a);
    }

    public VBox createOutputNode(String name, String strDataDef, Object value){
        return createOutputNode(name, DataDefinitionRegistry.valueOf(strDataDef), value);
    }
    public VBox createOutputNode(String name, DataDefinition dataDefinition, Object value) {
        if (dataDefinition.getName().equals("Relation")) {
            LinkedTreeMap<String, Object> theRelationAsMap = (LinkedTreeMap<String, Object>) value;
            Relation relation = new Relation((List<String>) theRelationAsMap.get("columnsNames"),
                                                (List<LinkedTreeMap>) theRelationAsMap.get("rows"),
                                                (LinkedTreeMap) theRelationAsMap.get("columns"));
            value = relation;
        }
        VBox backBone = new VBox();
        backBone.setSpacing(10);
        HBox nameAndType = new HBox();
        nameAndType.setSpacing(10);
        backBone.getChildren().add(nameAndType);
        Label nameLabel = new Label("Name: " + name);
        nameLabel.setWrapText(true);
        Label typeLabel = new Label("Type: " + dataDefinition);
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
