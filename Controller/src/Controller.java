import api.UIAbstractDefinition;
import impl.ConsoleUI;
import stepper.controller.EngineController;
import stepper.dto.DTO;
import stepper.dto.flow.LoadDataDTO;

import static java.lang.System.out;

public class Controller {
    boolean keepAlive = true;
    private final UIAbstractDefinition ui = new ConsoleUI();
    private final EngineController engineController = new EngineController();


    public void start(){
        while(keepAlive){
            ui.PresentMainMenu();
            handleUsersMainMenuSelection();
        }
    }
    private DTO readXML(String path){
        return engineController.readXML(path);
    }

    public static void main(String[] args) {
        Controller ctl = new Controller();
        ctl.start();
    }

    public String handleUsersMainMenuSelection() {
        String message;

        switch(ui.getUsersNumericResponse()){
            case 1:
                while(true) {
                    LoadDataDTO loadDataDTO = (LoadDataDTO) readXML(ui.createValidPath());
                    // if file was read successfully value will be true -> else false.
                    if(!loadDataDTO.getStatus()){
                        message = "The path received is invalid! please check that there are no hebrew characters involved!\n" +
                                "The exception received: "+loadDataDTO.getErrorMessage();
                        continue;
                    }
                    message = "The file was loaded successfully!";
                    ui.notifyUser(message);
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

