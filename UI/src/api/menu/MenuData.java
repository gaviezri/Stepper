package api.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuData {

    public Integer getMenuItemCount() {
        return menus.get(MenuNumber.MAIN_MENU.getId()).size()-1;
    }

    public enum MenuNumber{
        MAIN_MENU(0),
        SUB(1),
        ;
        final Integer id;


        MenuNumber(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }
    }
    private static MenuData instance = null;
    private final List<List<String>> menus = new ArrayList<>();

    private MenuData() {
        createMainMenu();
    }

    private void createMainMenu() {
        menus.add(new ArrayList<>());

        menus.get(MenuNumber.MAIN_MENU.getId()).add("Please select one of the following options:\n");
        menus.get(MenuNumber.MAIN_MENU.getId()).add("1. Load new XML file\n");      // load XML file by name ->
        // if loaded successfully show inform user.
        // if failed present to user what was wrong with the file!
        menus.get(MenuNumber.MAIN_MENU.getId()).add("2. Show flow definition\n");   // unfold loaded flows names list for the user to choose from,
        // 0 to return to main menu and another to load a new file
        menus.get(MenuNumber.MAIN_MENU.getId()).add("3. Execute flow\n");           // unfold loaded flows names list for the user to choose from,
        // 0 to return to main menu and another to load a new file
        menus.get(MenuNumber.MAIN_MENU.getId()).add("4. Show full details of past flow execution\n"); // show a sorted last-to-be-executed to first list of numbered executed flows
        menus.get(MenuNumber.MAIN_MENU.getId()).add("5. Get flows statistics\n");
        menus.get(MenuNumber.MAIN_MENU.getId()).add("6. Exit\n");
    }

    // this function is for options 3 & 4 -> need to add a 'back to main menu' option and 'load new file' option.
    private void createNumericListOfLoadedFlowsInSystem(){

    }

    public static MenuData getInstance() {
        if(instance == null){
            instance = new MenuData();
        }
        return instance;
    }

    public List<String> getMainMenu(){
        return menus.get(MenuNumber.MAIN_MENU.getId());
    }

}

