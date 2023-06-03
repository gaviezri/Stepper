package stepper.dd.api;

import com.sun.istack.internal.NotNull;
import stepper.dd.impl.DataDefinitionRegistry;

public interface DataDefinition {
    String getName();
    boolean isUserFriendly();
    Class<?> getType();
    String toString();
}
