package stepper.dd.impl.doublle;

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
}
