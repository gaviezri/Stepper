package api;

import api.menu.MenuData;

import java.util.List;

public abstract class UIAbstractDefinition {
    protected MenuData menuData = MenuData.getInstance();
    public abstract void PresentMainMenu();
    public abstract int getUsersNumericResponse(Integer max);


    public abstract int getUsersNumericResponse(Integer min, Integer max);

    public abstract String createValidPath();

    public abstract void presentLoadedFlowNames(List<String> flowNames);
}
