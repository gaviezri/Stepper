package stepper.dd.impl.relation;

import java.text.CollationKey;
import java.util.*;
import java.util.stream.Collectors;

public class RelationData extends RelationDataDefinition{

    private List<String> columnsNames;
    private List<SingleRow> rows = new ArrayList<>();
    private Map<String,List<String>> columns = new HashMap<>();

    int rowSize = 0;
    int colSize = 0;
    int totalSize = 0;

    public enum Element {
        ROW, COLUMN
    }

    public RelationData(List<String> columnsNames) {
        this.columnsNames = columnsNames;
        columnsNames.forEach(columnName -> columns.put(columnName, new ArrayList<>()));
        rowSize = colSize = totalSize = 0;
    }

    public List getDataFromRow(int index){

        List<String> dataFromRow = new ArrayList<>();
        for (String columnName : columnsNames) {
            dataFromRow.add(columns.get(columnName).get(index));
        }
        return dataFromRow;
    }
    public List getDataFromColumn(String columnName){
        return columns.get(columnName);
    }
    public String getDataFromCell(int rowIndex, String columnName){
        return columns.get(columnName).get(rowIndex);
    }
    public void addRow(List<String> row){

        for (int i = 0; i < row.size(); i++) {
            columns.get(columnsNames.get(i)).add(row.get(i));
            totalSize +=  row.size();
            rowSize += row.size();
        }
    }
    public void addColumn(String columnName, List<String> column){

        columns.put(columnName, column);
        rows.forEach(row -> row.addData(columnName, null));
        columnsNames.add(columnName);
        colSize += 1;
        totalSize += column.size();
    }

    public int getRowSize() {
        return rowSize;
    }
    public int getColSize() {
        return colSize;
    }
    public int getTotalSize() {
        return totalSize;
    }

    //TODO: ?? DELETE ??

    private static class SingleRow {

        private Map<String, String> data;

        public SingleRow() {
            data = new HashMap<>();
        }

        public void addData(String columnName, String value) {
            data.put(columnName, value);
        }
    }
}
