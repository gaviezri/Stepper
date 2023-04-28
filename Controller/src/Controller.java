import api.UIAbstractDefinition;
import controller.UIController;
import stepper.controller.EngineController;
import stepper.dto.DTO;

public class Controller {
    boolean keepAlive = true;
    private final UIController uiController = null;
    private final EngineController engineController = new EngineController();


    public void start(){
        while(keepAlive){
            uiController.
        }
    }
    private void readXML(String path){
        DTO dto = engineController.readXML(path);
    }



}
