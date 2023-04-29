package api;

import api.menu.MenuData;

import java.util.List;

public abstract class UIAbstractDefinition {
    protected MenuData menuData = MenuData.getInstance();

    public MenuData getMenuData() {
        return menuData;
    }

    public abstract void PresentMainMenu();
    /** gets inputs from user and validate that it is in range 0 (back to main menu fixed option) - max option index
     * received as input for this function*/
    public abstract int getUsersNumericResponse(Integer max);

    public abstract String createValidPath();
    /** the names of flows are presented to user by their index+1*/
    public abstract void presentLoadedFlowNames(List<String> flowNames);
    /** a function that gets a message and present it to the user in the way the UI works*/
    public abstract void presentMessageToUser(String message);
    /** show the user all flows names and gets response. then subtracts 1 so that the index will fit.
     * if the user response is 0 (back to main menu) -1 will be the output of the function*/
    public abstract Integer getSelectedFlowIndexFromUser(List<String> flowNamesDTO);
    public abstract void presentBackToMainMenuOption();

    public abstract void getInputsFromUser();
}
