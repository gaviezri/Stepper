package stepper.dd.impl.number;

public class NumberData extends NumberDataDefinition {
    Integer data;

    public NumberData(Integer data) {
        this.data = data;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }
}
