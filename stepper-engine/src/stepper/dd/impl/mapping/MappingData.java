package stepper.dd.impl.mapping;

import stepper.dd.api.AbstractDataDefinition;
import stepper.dd.impl.number.NumberData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MappingData extends MappingDataDefinition{
    private AbstractDataDefinition car;
    private AbstractDataDefinition cdr;

    public MappingData() {
        this.car = new NumberData(0);
        this.cdr = new NumberData(0);
    };

    public MappingData(AbstractDataDefinition car, AbstractDataDefinition cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public AbstractDataDefinition getCar() {
        return car;
    }

    public void setCar(AbstractDataDefinition car) {
        this.car = car;
    }

    public AbstractDataDefinition getCdr() {
        return cdr;
    }

    public void setCdr(AbstractDataDefinition cdr) {
        this.cdr = cdr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MappingData that = (MappingData) o;
        return Objects.equals(getCar(), that.getCar()) && Objects.equals(getCdr(), that.getCdr());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCar(), getCdr());
    }
}
