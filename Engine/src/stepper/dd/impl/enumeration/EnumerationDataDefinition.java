package stepper.dd.impl.enumeration;

import stepper.dd.api.AbstractDataDefinition;
public class EnumerationDataDefinition  extends AbstractDataDefinition{
    public EnumerationDataDefinition() {
        super("Enumeration", true, java.lang.Enum.class);
    }
}
