package stepper.step.api;

import stepper.dd.api.DataDefinition;
import stepper.step.api.enums.DataNecessity;

import java.util.Objects;

public class DataDefinitionDeclarationImpl implements DataDefinitionDeclaration {

    private final String name;
    private final DataNecessity necessity;
    private final String userString;
    private final DataDefinition dataDefinition;

    public DataDefinitionDeclarationImpl(String name, DataNecessity necessity, String userString, DataDefinition dataDefinition) {
        this.name = name;
        this.necessity = necessity;
        this.userString = userString;
        this.dataDefinition = dataDefinition;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getType() {
        return dataDefinition.getType();
    }

    @Override
    public DataNecessity necessity() {
        return necessity;
    }

    @Override
    public String userString() {
        return userString;
    }

    @Override
    public DataDefinition dataDefinition() {
        return dataDefinition;
    }

    @Override
    public boolean isUserFriendly() {
        return dataDefinition.isUserFriendly();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataDefinitionDeclarationImpl)) return false;
        DataDefinitionDeclarationImpl that = (DataDefinitionDeclarationImpl) o;
        return Objects.equals(getName(), that.getName()) && necessity == that.necessity && Objects.equals(userString, that.userString) && Objects.equals(dataDefinition, that.dataDefinition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), necessity, userString, dataDefinition);
    }
}
