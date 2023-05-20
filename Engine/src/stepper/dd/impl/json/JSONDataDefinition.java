package stepper.dd.impl.json;

import stepper.dd.api.AbstractDataDefinition;

public class JSONDataDefinition extends AbstractDataDefinition {
    public JSONDataDefinition() {
        super("JSON", true, String.class);
    }
}
