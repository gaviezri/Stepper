package stepper.dd.impl.string;

public class StringData extends StringDataDefinition {
    String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public StringData(String data) {
        this.data = data;
    }
}
