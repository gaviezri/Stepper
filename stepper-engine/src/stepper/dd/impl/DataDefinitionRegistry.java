package stepper.dd.impl;

import stepper.dd.api.DataDefinition;
import stepper.dd.impl.list.ListDataDefinition;
import stepper.dd.impl.number.DoubleDataDefinition;
import stepper.dd.impl.number.NumberDataDefinition;
import stepper.dd.impl.relation.RelationDataDefinition;
import stepper.dd.impl.string.StringDataDefinition;

public enum DataDefinitionRegistry implements DataDefinition{
    STRING(new StringDataDefinition()),
    NUMBER(new NumberDataDefinition()),
    DOUBLE(new DoubleDataDefinition()),
    RELATION(new RelationDataDefinition()),
    LIST(new ListDataDefinition())
    ;

    DataDefinitionRegistry(DataDefinition dataDefinition) {
        this.dataDefinition = dataDefinition;
    }

    private final DataDefinition dataDefinition;

    @Override
    public String getName() {
        return dataDefinition.getName();
    }

    @Override
    public boolean isUserFriendly() {
        return dataDefinition.isUserFriendly();
    }

    @Override
    public Class<?> getType() {
        return dataDefinition.getType();
    }
}
