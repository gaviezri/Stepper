package api;

import api.menu.MenuData;

import java.util.List;
import java.util.Map;

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
    /** gets inputs from user and validate that it is in range 0 (back to main menu fixed option) - max option index
     * received as input for this function*/
    public abstract int getUsersNumericResponse(Integer max);

    public abstract String createValidPath();
    /** the names of flows are presented to user by their index+1*/
    public abstract void presentLoadedFlowNames(List<String> flowNames);
    /** a function that gets a message and present it to the user in the way the UI works*/
    public abstract void presentMessageToUser(String message);

    public abstract int getUsersNumericResponse(Integer min, Integer max);

    /** show the user all flows names and gets response. then subtracts 1 so that the index will fit.
     * if the user response is 0 (back to main menu) -1 will be the output of the function*/
    public abstract Integer getSelectedFlowIndexFromUser(List<String> flowNamesDTO);
    public abstract void presentBackToMainMenuOption();
    /** inputs:
     * 1. final names of free inputs
     * 2. types of the inputs  expected
     * 3. necessity of the inputs (mandatory\optional)
     * all inputs correspond by index.
     *
     * the UI will present the free inputs one by one,mandatory first, and get data from user.
     * when all mandatory inputs are received the user will be presented with an 'Execute option'
     *
     * output:
     * Map<"input_name : type" , object>
     * */
    public abstract Map<String,Object> getInputsFromUser(List<String> freeInputsFinalNames, List<String> freeInputTypes, List<String> freeInputNecessity);

}
