package stepper.dd.impl;

import stepper.dd.impl.enumeration.EnumerationDataDefinition;
import stepper.dd.impl.list.ListDataDefinition;
import stepper.dd.impl.number.DoubleDataDefinition;
import stepper.dd.impl.mapping.MappingDataDefinition;
import stepper.dd.impl.number.NumberDataDefinition;
import stepper.dd.impl.relation.RelationDataDefinition;
import stepper.dd.impl.string.StringDataDefinition;

public enum DataDefinitionRegistry implements stepper.dd.api.DataDefinition {
    STRING(new StringDataDefinition()),
    NUMBER(new NumberDataDefinition()),
    DOUBLE(new DoubleDataDefinition()),
    RELATION(new RelationDataDefinition()),
    LIST(new ListDataDefinition()),
    MAPPING(new MappingDataDefinition()),
    ENUMERATION(new EnumerationDataDefinition());

    DataDefinitionRegistry(stepper.dd.api.DataDefinition dataDefinition) {
        this.dataDefinition = dataDefinition;
    }

    private final stepper.dd.api.DataDefinition dataDefinition;

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
