package stepper.dd.impl.list;

import stepper.dd.api.AbstractDataDefinition;

import java.util.List;

public class ListDataDefinition extends AbstractDataDefinition {
    public ListDataDefinition() {
        super("List", false, List.class);
    }

}
