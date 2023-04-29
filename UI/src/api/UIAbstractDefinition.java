package api;

import api.menu.MenuData;

import java.util.List;

public abstract class UIAbstractDefinition {
    protected MenuData menuData = MenuData.getInstance();

    public MenuData getMenuData() {
        return menuData;
    }
    /** Returns the number of menu items in the main menu
     * not including the header line
     * @return int
     */
    public int getMenuItemCount(){

        return menuData.getMainMenu().size()-1;
    }

    public abstract void PresentMainMenu();

    public abstract int getUsersNumericResponse(Integer max);

    public abstract String createValidPath();

    public abstract void presentLoadedFlowNames(List<String> flowNames);

    public abstract void presentMessageToUser(String message);

    public abstract int getUsersNumericResponse(Integer min, Integer max);
}
