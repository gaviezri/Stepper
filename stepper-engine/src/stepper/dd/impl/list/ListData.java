package stepper.dd.impl.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListData extends ListDataDefinition {

    private List data = new ArrayList();
    private int size = 0;

    public ListData() {}
    public ListData(List data) {
        this.data = data;
        this.size = data.size();
    }
    public void add(Object data) {
        this.data.add(data);
        this.size += 1;
    }
    public Object get(int index) {
        return this.data.get(index);
    }
    public int size() {
        return this.size;
    }
    public List getAll() {
        return this.data;
    }
    public void setData(List data) {
        this.data = data;
        this.size = data.size();
    }
    public void removeData(int index) {
        this.data.remove(index);
        this.size -= 1;
    }

}
