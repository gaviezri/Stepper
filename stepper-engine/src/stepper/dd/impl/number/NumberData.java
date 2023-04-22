package stepper.dd.impl.number;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberData)) return false;
        NumberData that = (NumberData) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
