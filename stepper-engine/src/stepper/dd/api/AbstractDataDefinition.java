package stepper.dd.api;

import java.util.Objects;

public abstract class AbstractDataDefinition implements DataDefinition {
    private final String name;
    private final boolean userFriendly;
    private final Class<?> type;
    // protected abstract <T> T toDTO(Class<T> dtoType, Object data);

    @Override
    public String toString() {
        return null;
    }

    public AbstractDataDefinition(String name, boolean userFriendly, Class<?> type) {
        this.name = name;
        this.userFriendly = userFriendly;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isUserFriendly() {
        return userFriendly;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractDataDefinition)) return false;
        AbstractDataDefinition that = (AbstractDataDefinition) o;
        return isUserFriendly() == that.isUserFriendly() && Objects.equals(getName(), that.getName()) && Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), isUserFriendly(), getType());
    }
}
