package stepper.dd.impl.mapping;

import stepper.dd.api.AbstractDataDefinition;

public class MappingDataDefinition extends AbstractDataDefinition {
    public MappingDataDefinition() { super("mapping", false, MappingData.class);
    }

    @Override
    protected String presentToUser() {
        return null;
    }
}
