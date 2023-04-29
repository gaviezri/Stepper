import api.UIAbstractDefinition;
import impl.ConsoleUI;
import javafx.util.Pair;
import stepper.controller.EngineController;
import stepper.dto.DTO;
import stepper.dto.flow.FlowDefinitionDTO;
import stepper.dto.flow.FlowNamesDTO;
import stepper.dto.flow.LoadDataDTO;

import java.util.List;
import java.util.Map;

public class Controller {
    boolean keepAlive = true;
    private final UIAbstractDefinition ui = new ConsoleUI();
    private final EngineController engineController = new EngineController();


    public void start() {
        try {
            ui.presentMessageToUser("#####################################################");
            ui.presentMessageToUser("# Welcome To \"Stepper\" by Omri Shahar & Gal Aviezri #");
            ui.presentMessageToUser("#####################################################\n");
            Thread.sleep(777);
            while (keepAlive) {
                ui.PresentMainMenu();
                handleUsersMainMenuSelection();

            }
            ui.presentMessageToUser("\n\nGoodbye!\n");
            Thread.sleep(777);

        }
        catch (Exception e){
            ui.presentMessageToUser("Error: " + e.getMessage());
        }
    }
    private LoadDataDTO readXML(String path){
        return (LoadDataDTO) engineController.readXML(path);
    }
    private FlowNamesDTO getFlowDefinitionsNames(){
        return (FlowNamesDTO) engineController.getFlowDefinitionsNames();
    }

    public void handleUsersMainMenuSelection() {


        switch(ui.getUsersNumericResponse(1,ui.getMenuData().getMenuItemCount())){
            case 1:
                loadingNewXmlFile();
                break;
            case 2:
                presentFlowDefinitions();
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                keepAlive = false;
                break;

        }
    }



    private void loadingNewXmlFile() {
        boolean flag = true;
        while (flag) {
            String message;
            ui.presentMessageToUser("Please enter a full-path to the XML file you desire to load: ");
            LoadDataDTO loadDataDTO = readXML(ui.createValidPath());

            if (!loadDataDTO.getStatus()) {
                message = "Loading file from path failed\n" +
                        "please check the path given, make sure no hebrew words are included!\n";
                message = loadDataDTO.getErrorMessage().isEmpty() ? message : message + "Error message: " + loadDataDTO.getErrorMessage();
            } else {
                message = loadDataDTO.getMessage() + "\nreturning to main menu for further actions";
                flag = false;
            }

            ui.presentMessageToUser(message);
        }
    }

    /** show the user a list of all the loaded flows in the system, and let him choose one to further inspect its definition */
    private void presentFlowDefinitions() {
        boolean flag = true;
        FlowNamesDTO flowNamesDTO = getFlowDefinitionsNames();
        if (!flowNamesDTO.getStatus()) {
            ui.presentMessageToUser("Error: " + flowNamesDTO.getErrorMessage() + "\nreturning to main menu for further actions");

        } else {
            Integer selection = 0;
            List<String> flowNames = flowNamesDTO.getFlowNames();
            while (flag) {

                selection = selectionOfFlowToShow(flowNames);
                if (selection == 0) {
                    flag = false;
                } else {
                    DTO flowDefDTO = engineController.getFlowDefinition(selection-1);

                    if (flowDefDTO.getClass()==LoadDataDTO.class){
                        ui.presentMessageToUser("Error: " + flowDefDTO.getErrorMessage());
                        flag = false;
                    }else {
                       presentDefinition(flowDefDTO);
                    }
                    ui.presentMessageToUser("\nreturning to main menu for further actions");
                }
            }
        }
    }

    private void presentDefinition(DTO flowDefDTO) {
        FlowDefinitionDTO flowDefinitionDTO = (FlowDefinitionDTO) flowDefDTO;
        ui.presentMessageToUser("\nFlow Name: " + flowDefinitionDTO.getFlowName());
        ui.presentMessageToUser("Description: " + flowDefinitionDTO.getDescription());
        ui.presentMessageToUser("Formal Outputs: " + flowDefinitionDTO.getFormalOutputs());
        ui.presentMessageToUser("Readonly: " + flowDefinitionDTO.getFlowReadonly());
        ui.presentMessageToUser("Steps: ");
        presentSteps(flowDefinitionDTO);
        ui.presentMessageToUser("Free Inputs: ");
        presentFreeInputs(flowDefinitionDTO);
        ui.presentMessageToUser("Outputs: ");
        presentOutputs(flowDefinitionDTO);
    }

    private void presentOutputs(FlowDefinitionDTO flowDefinitionDTO) {
        List<String> outputs = flowDefinitionDTO.getOutputsFinalNames();
        List<String> outputTypes = flowDefinitionDTO.getOutputTypes();
        List<String> stepsThatUseOutputs = flowDefinitionDTO.getFinalStepNameThatProducedTheOutput();
        for (int i = 0; i < outputs.size(); i++) {
            String presentation = outputPresentation(outputs.get(i),
                                                     outputTypes.get(i),
                                                     stepsThatUseOutputs.get(i));
            ui.presentMessageToUser((i+1)+". " + presentation);
        }
    }

    private String outputPresentation(String name, String type, String stepProducedBy) {
        return "Name: " + name + " - Type: <" + type + "> - Produced by (Step): " + stepProducedBy;
    }

    private void presentFreeInputs(FlowDefinitionDTO flowDefinitionDTO) {
        List<String> freeInputs = flowDefinitionDTO.getFreeInputsFinalNames();
        List<String> freeInputTypes = flowDefinitionDTO.getFreeInputTypes();
        Map<String,List<String>> stepsThatUseFreeInputs = flowDefinitionDTO.getFreeInputs2StepsThatUseThem();
        List<String> freeInputsNecessity = flowDefinitionDTO.getFreeInputNecessity();
        for (int i = 0; i < freeInputs.size(); i++) {
            String name = freeInputs.get(i);
            String presentation = freeInputPresentation(name,
                                                        freeInputTypes.get(i),
                                                        stepsThatUseFreeInputs.get(name),
                                                        freeInputsNecessity.get(i));
            ui.presentMessageToUser((i+1)+". " + presentation);
        }
    }

    private void presentSteps(FlowDefinitionDTO flowDefinitionDTO) {
        List<Pair<String,String>> steps2alias = flowDefinitionDTO.getStep2alias();
        List<Boolean> stepReadonly = flowDefinitionDTO.getIsStepReadonly();
        for (int i = 0; i < steps2alias.size(); i++) {
            String presentation = stepPresentation(steps2alias, stepReadonly, i);
            ui.presentMessageToUser((i+1)+". " + presentation);
        }
    }

    private String freeInputPresentation(String name, String type, List<String> stepsThatUseMe, String Necessity) {
        String presentation = "Name: " + name + " - " + "Type: <" +type + "> - " + "Necessity: " + Necessity + " - used by (steps): ";
        for (String step : stepsThatUseMe) {
            presentation = presentation + step + ", ";
        }
        return presentation.substring(0,presentation.length()-2);
    }

    private static String stepPresentation(List<Pair<String, String>> steps2alias, List<Boolean> stepReadonly, int i) {
        String name = steps2alias.get(i).getKey();
        String alias = steps2alias.get(i).getValue();
        Boolean gotAlias = !name.equals(alias);
        String presentation = alias + (gotAlias ? " - (" + name + ")" : "") + " readonly: " + stepReadonly.get(i);
        return presentation;
    }

    private Integer selectionOfFlowToShow(List<String> flowNames) {
        String message;
        ui.presentMessageToUser("Please select one of the following flows to further inspect its definition: ");
        for(int i = 0; i < flowNames.size(); i++){
            ui.presentMessageToUser((i+1) + ". " + flowNames.get(i));
        }
        ui.presentMessageToUser("0. Return to main menu");
       return ui.getUsersNumericResponse(0, flowNames.size());
    }

    public static void main(String[] args) {
        Controller ctl = new Controller();
        ctl.start();
    }

}

