package stepper.dd.impl.relation;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class RelationData extends RelationDataDefinition implements Serializable {

    private List<String> columnsNames;
    private List<SingleRow> rows = new ArrayList<>();
    private Map<String,List<String>> columns = new LinkedHashMap<>();

    int rowSize = 0;
    int colSize = 0;
    int totalSize = 0;


    public RelationData(List<String> columnsNames) {
        this.columnsNames = columnsNames;
        columnsNames.forEach(columnName -> columns.put(columnName, new ArrayList<>()));
        rowSize = totalSize = 0;
        colSize = columnsNames.size();
    }
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("RelationData: \n");
        sb.append("Columns names: ");
        columnsNames.forEach(columnName -> sb.append("\"" + columnName + "\"").append(" | "));
        sb.append("\n");
        sb.append("Number of rows: " + rowSize + "\n");
        rows.forEach(row -> sb.append(row.toString()).append("\n"));
        return sb.toString();
    }
    public List<String> getColumnsNames() {
        return columnsNames;
    }
    public List getDataFromRow(int index){

        List<String> dataFromRow = new ArrayList<>();
        for (String columnName : columnsNames) {
            dataFromRow.add(rows.get(index).getData(columnName));
        }
        return dataFromRow;
    }

    public List<String> getColumnNames(){
        return columnsNames;
    }
    public List getDataFromColumn(String columnName){
        return columns.get(columnName);
    }
    public String getDataFromCell(int rowIndex, int columnIndex){
        List<String> column = columns.get(columnsNames.get(columnIndex));
        return column.get(rowIndex);
    }
    public void addRow(List<String> row){
        SingleRow singleRow = new SingleRow();
        for (int i = 0; i < row.size(); i++) {
            columns.get(columnsNames.get(i)).add(row.get(i));
            singleRow.addData(columnsNames.get(i), row.get(i));
            totalSize++;
        }
        rows.add(singleRow);
        rowSize ++;
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

    public List<Map<String,String>> getRows() {
        return rows.stream().map(row -> row.data).collect(Collectors.toList());
    }


    public static class SingleRow implements Serializable{

        private Map<String, String> data;

        public SingleRow() {
            data = new HashMap<>();
        }
        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            data.forEach((columnName, value) -> sb.append("\"" + columnName + "\"").append(" : ").append("\"" + value + "\"").append(" | "));
            return sb.toString();
        }

        public void addData(String columnName, String value) {
            data.put(columnName, value);
        }
        public String getData(String columnName) {
            return data.get(columnName);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelationData)) return false;
        RelationData that = (RelationData) o;
        return getRowSize() == that.getRowSize() && getColSize() == that.getColSize() && getTotalSize() == that.getTotalSize() && Objects.equals(getColumnsNames(), that.getColumnsNames()) && Objects.equals(rows, that.rows) && Objects.equals(columns, that.columns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getColumnsNames(), rows, columns, getRowSize(), getColSize(), getTotalSize());
    }
}
