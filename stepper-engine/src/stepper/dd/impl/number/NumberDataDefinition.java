package stepper.dd.impl.number;

import stepper.dd.api.AbstractDataDefinition;

public class NumberDataDefinition extends AbstractDataDefinition {
    public NumberDataDefinition() {
        super("Number", true, Number.class);
    }

    @Override
    protected String presentToUser() {
        return null;
    }
}
