package stepper.dd.impl.doublle;

import java.util.Objects;

public class DoubleData extends DoubleDataDefinition{

    private Double data;

    public Double getData() {
        return data;
    }
    public void setData(Double data) {
        this.data = data;
    }

    public DoubleData(Double data) {
        this.data = data;
    }

    @Override
    protected String presentToUser() {
        return data.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoubleData)) return false;
        DoubleData that = (DoubleData) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
