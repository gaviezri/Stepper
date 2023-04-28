package api;

import api.menu.MenuData;

public abstract class UIAbstractDefinition {
    protected MenuData menuData = MenuData.getInstance();
    public abstract void PresentMainMenu();
    public abstract int getUsersNumericResponse();

    public String createValidPath() {}
}
