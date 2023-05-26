package stepper.step.api;

import stepper.dd.api.DataDefinition;
import stepper.step.api.enums.DataNecessity;

public interface DataDefinitionDeclaration {
    String getName();
    DataNecessity necessity();
    String userString();
    DataDefinition dataDefinition();
    boolean isUserFriendly();
    Class<?> getType();
}
