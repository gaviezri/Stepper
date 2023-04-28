import api.UIAbstractDefinition;
import impl.ConsoleUI;
import stepper.controller.EngineController;
import stepper.dto.DTO;

import static java.lang.System.out;

public class Controller {
    boolean keepAlive = true;
    private final UIAbstractDefinition ui = new ConsoleUI();
    private final EngineController engineController = new EngineController();


    public void start(){
        while(keepAlive){
            ui.PresentMainMenu();
            ui.handleUsersMainMenuSelection();
            keepAlive = false;
        }
    }
    private void readXML(String path){
        DTO dto = engineController.readXML(path);
    }

    public static void main(String[] args) {
        Controller ctl = new Controller();
        ctl.start();
    }

    public String handleUsersMainMenuSelection() {
        switch(ui.getUsersNumericResponse()){
            case 1:
                while(true) {
                    out.println("Please enter a full-path to the XML file you desire to load: ");
                    readXML(ui.createValidPath());
                }
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
        }
        return null;
    }
}

