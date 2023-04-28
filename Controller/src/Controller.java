import api.UIAbstractDefinition;
import impl.ConsoleUI;
import stepper.controller.EngineController;
import stepper.dto.flow.LoadDataDTO;

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
    private LoadDataDTO readXML(String path){
        return (LoadDataDTO) engineController.readXML(path);
    }

    public static void main(String[] args) {
        Controller ctl = new Controller();
        ctl.start();
    }

    public void handleUsersMainMenuSelection() {
        boolean flag = true;

        switch(ui.getUsersNumericResponse(ui.getMenuData().getMainMenu().size())){
            case 1:
                while(flag) {
                    flag = loadingNewXmlFile(flag);
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
    }

    private boolean loadingNewXmlFile(boolean flag) {
        String message;
        ui.presentMessageToUser("Please enter a full-path to the XML file you desire to load: ");
        LoadDataDTO loadDataDTO = readXML(ui.createValidPath());

        if(!loadDataDTO.getStatus()){
            message = "Loading file from path failed\n" +
                    "please check the path given, make sure no hebrew words are included!\n" +
                    "The error message received: "+loadDataDTO.getErrorMessage();
        }
        else{
            message ="File loaded successfully!\n returning to main menu for further actions";
            flag = false;
        }

        ui.presentMessageToUser(message);

        return flag;
    }
}

