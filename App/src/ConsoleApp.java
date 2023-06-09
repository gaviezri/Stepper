import api.UIAbstractDefinition;
import impl.ConsoleUI;
import javafx.util.Pair;
import stepper.controller.EngineController;
import stepper.dto.AbstractDTO;
import stepper.dto.flow.ExecutedFlowDetailsDTO;
import stepper.dto.flow.FlowDefinitionDTO;
import stepper.dto.flow.FlowNamesDTO;
import stepper.dto.flow.LoadDataDTO;
import stepper.statistics.StatisticsManager;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConsoleApp {
    boolean keepAlive = true;
    private final UIAbstractDefinition ui = new ConsoleUI();
    private EngineController engineController = EngineController.getInstance();
    private  StatisticsManager statisticsManager = new StatisticsManager(engineController.getArchive());


    public void start() {
        ui.presentMessageToUser("\n\n#####################################################");
        ui.presentMessageToUser("# Welcome To \"Stepper\" by Omri Shahar & Gal Aviezri #");
        ui.presentMessageToUser("#####################################################\n");
        while (keepAlive) {
            try {
                ui.PresentMainMenu();
                handleUsersMainMenuSelection();
            } catch (Exception e) {
                ui.presentMessageToUser("Error: " + e.getMessage());
            }
        }
        ui.presentMessageToUser("\n\nGoodbye!\n");
    }

    private LoadDataDTO readXML(String path){
        return  engineController.readXML(path);
    }
    private FlowNamesDTO getFlowDefinitionsNames(){
        return engineController.getFlowDefinitionsNames();
    }

    public void handleUsersMainMenuSelection() {

        switch(ui.getUsersNumericResponse(1,ui.getMenuData().getMenuItemCount())) {
            case 1:
                loadingNewXmlFile();
                break;
            case 2:
                presentFlowDefinitions();
                break;
            case 3:
                executeFlow();
                break;
            case 4:
                presentFlowExecutionDetails();
                break;
            case 5:
                presentStatistics();
                break;
            case 6:
                saveSystemState();
                break;
            case 7:
                loadSystemState();
                break;
            case 8:
                keepAlive = false;
                break;
        }
    }

    private void loadSystemState() {
        ui.presentMessageToUser("Attention! loading a system from file will overwrite its current state.");
        ui.presentMessageToUser("to load system state from file, enter the \"stepper.ser\" path to load the system state from:");
        String ser_path  = ui.createValidPath(false);
        while(!Files.exists(Paths.get(ser_path))){
            ui.presentMessageToUser("Error: file does not exist! please enter a valid path to a file or \'EXIT\' to go back to main menu:");
            ser_path  = ui.createValidPath(false);
            if (ser_path.equals("EXIT")){
                return;
            }
        }
        loadSystemStateFromPath(ser_path);
    }

    private void loadSystemStateFromPath(String serPath) {
        try {
            FileInputStream FileInStream = new FileInputStream(serPath + "\\stepper.ser");
            ObjectInputStream ObjectInStream = new ObjectInputStream(FileInStream);
            engineController = (EngineController) ObjectInStream.readObject();
            ObjectInStream.close();
            FileInStream.close();
            ui.presentMessageToUser("System state loaded successfully from " + serPath + "\\stepper.ser");
        }catch (Exception e){
            ui.presentMessageToUser("Error: " + e.getMessage());
            ui.presentMessageToUser("System state could not be loaded!");
        }
    }

    private void saveSystemState() {
        ui.presentMessageToUser("To save system state, enter Directory path to save the system state at:");
        String dirpath  = ui.createValidPath(true);
        saveSystemStateToPath(dirpath);
    }

    private void saveSystemStateToPath(String dirpath) {
        try {
            FileOutputStream FileOutStream = new FileOutputStream(dirpath + "\\stepper.ser");
            ObjectOutputStream ObjectOutStream = new ObjectOutputStream(FileOutStream);
            ObjectOutStream.writeObject(engineController);
            ObjectOutStream.close();
            FileOutStream.close();
            ui.presentMessageToUser("System state saved successfully @ " + dirpath + "\\stepper.ser");
        }catch (Exception e){
            ui.presentMessageToUser("Error: " + e.getMessage());
            ui.presentMessageToUser("System state was not saved!");
        }
    }

    private void presentFlowExecutionDetails() {
        if (engineController.executionOccurred()){
            handlePresentationOfExecutionDetails();
        }
        else{
            ui.presentMessageToUser("Error: No Flows Executed Yet! Maybe execute a flow and try again...\nreturning to main menu for further actions");
        }

    }

    private void handlePresentationOfExecutionDetails() {
        while(true) {
            ui.presentMessageToUser("\nchoose a flow to present its execution details:");
            int choice = presentExecutionDetailsHeaders(engineController.getExecutedFlowHeaders());
            if (choice == 0){
                break;
            }
            else{
               presentExtensiveExecutionDetails(engineController.getExecutedFlowDetails(choice-1));
            }
        }
    }

    private void presentExtensiveExecutionDetails(ExecutedFlowDetailsDTO executedFlowDetailsDTO){
        presentFlowHeader(executedFlowDetailsDTO);
        presentFlowFreeInputsDetails(executedFlowDetailsDTO);
        presentFlowOutputsDetails(executedFlowDetailsDTO);
        presentStepsDetails(executedFlowDetailsDTO);
    }

    private void presentStepsDetails(ExecutedFlowDetailsDTO executedFlowDetailsDTO) {
        List<String> stepsFinalNames = executedFlowDetailsDTO.getStepsNamesWithAlias();
        List<String> stepsDuration = executedFlowDetailsDTO.getStepsDurationInMillis();
        List<String> stepsResult = executedFlowDetailsDTO.getStepsResult();
        List<String> stepsSummaryLines = executedFlowDetailsDTO.getStepsSummaryLine();
        List<List<Pair<String, String>>> stepsLogs = executedFlowDetailsDTO.getStepsLogs2TimeStamp();
        ui.presentMessageToUser("\n-------------------------------------\nSTEPS:");
        for (int i = 0; i < stepsFinalNames.size(); i++){
            ui.presentMessageToUser("Name: "+ stepsFinalNames.get(i));
            ui.presentMessageToUser("Duration: " + stepsDuration.get(i));
            ui.presentMessageToUser("Result: " + stepsResult.get(i));
            if (stepsResult.get(i).equals("step did not run.")){
                continue;
            }
            ui.presentMessageToUser("Summary Line: " + stepsSummaryLines.get(i));
            ui.presentMessageToUser("Logs:");
            for (Pair<String,String> step2timestamp : stepsLogs.get(i)){
                ui.presentMessageToUser(step2timestamp.getValue()+ ": " + step2timestamp.getKey());
            }
            ui.presentMessageToUser("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        }
    }

    private void presentFlowOutputsDetails(ExecutedFlowDetailsDTO executedFlowDetailsDTO) {
        List<String> outputsFinalNames = executedFlowDetailsDTO.getOutputsFinalNames();
        List<Object> outputsValues = executedFlowDetailsDTO.getOutputsContent();
        List<String> outputsTypes = executedFlowDetailsDTO.getOutputsTypes();
        ui.presentMessageToUser("\n-------------------------------------\nOUTPUTS:\n");
        for(int i = 0; i < outputsFinalNames.size(); i++){
            ui.printResource(outputsFinalNames.get(i), outputsValues.get(i).toString(), outputsTypes.get(i));
        }
    }

    private void presentFlowFreeInputsDetails(ExecutedFlowDetailsDTO executedFlowDetailsDTO) {
        List<String> freeInputsFinalNames = executedFlowDetailsDTO.getFreeInputsFinalNames();
        List<String> freeInputsValues = executedFlowDetailsDTO.getFreeInputsContent();
        List<String> freeInputsTypes = executedFlowDetailsDTO.getFreeInputsTypes();
        List<String> freeInputsNecessity = executedFlowDetailsDTO.getFreeInputsNecessity();
        List<Boolean> isMandatory = executedFlowDetailsDTO.getFreeInputsNecessity().stream().map(x -> x.equals("MANDATORY")).collect(Collectors.toList());
        ui.presentMessageToUser("\n-------------------------------------\nFREE INPUTS:\n");
        // print mandatory first
        for (int i = 0; i < freeInputsFinalNames.size(); i++) {
            if(isMandatory.get(i)) {
                ui.printResource(freeInputsFinalNames.get(i), freeInputsValues.get(i), freeInputsTypes.get(i));
                ui.presentMessageToUser("Necessity: " + freeInputsNecessity.get(i));
                ui.presentMessageToUser("\n");
            }
        }
        // and then optional
        for (int i = 0; i < freeInputsFinalNames.size(); i++) {
            if(!isMandatory.get(i)) {
                ui.printResource(freeInputsFinalNames.get(i), freeInputsValues.get(i), freeInputsTypes.get(i));
                ui.presentMessageToUser("Necessity: " + freeInputsNecessity.get(i));
                ui.presentMessageToUser("\n");
            }
        }
    }

    private void presentFlowHeader(ExecutedFlowDetailsDTO executedFlowDetailsDTO) {
        ui.presentMessageToUser("------------FLOW DETAILS------------");
        ui.presentMessageToUser("Flow Unique ID: " + executedFlowDetailsDTO.getFlowExecutionId());
        ui.presentMessageToUser("Flow Name: " + executedFlowDetailsDTO.getFlowName());
        ui.presentMessageToUser("Flow Execution Result: " + executedFlowDetailsDTO.getFlowExecutionResult());
        ui.presentMessageToUser("Flow Execution Duration in Milliseconds: " + executedFlowDetailsDTO.getExecutionTimeInMillis());
    }

    private int presentExecutionDetailsHeaders(List<Map<String, String>> executedFlowHeaders) {

        for (int i = 0; i < executedFlowHeaders.size(); i++) {
            presentHeader(executedFlowHeaders, i);
        }
        ui.presentMessageToUser("-----------------------------------------");
        ui.presentMessageToUser("0. Return to main menu");
        ui.presentMessageToUser("Please select a the number corresponding to the flow\nthat you wish view its extended execution details:");
        return ui.getUsersNumericResponse(0, executedFlowHeaders.size());
    }

    private void presentHeader(List<Map<String, String>> executedFlowHeaders, int i) {
        ui.presentMessageToUser("-----------------------------------------");
        ui.presentMessageToUser((i +1) + ". " + "Name: " + executedFlowHeaders.get(i).get("Name") + "\nUUID: " + executedFlowHeaders.get(i).get("ID") + "\nExecution Time: " + executedFlowHeaders.get(i).get("Time"));
    }

    private void executeFlow() {
        if (engineController.isLoaded()){
            handlePossibleExecution();
        }
        else{
            ui.presentMessageToUser("Error: No Flows Loaded Yet!\nreturning to main menu for further actions");
        }
    }
    private void handlePossibleExecution(){
        FlowNamesDTO flowNamesDTO = engineController.getFlowDefinitionsNames();
        Integer selectedFlowIndex = ui.getSelectedFlowIndexFromUser(flowNamesDTO.getFlowNames());
        if(selectedFlowIndex >= 0) {
            FlowDefinitionDTO flowDefinitionDTO = engineController.getFlowDefinitionDataByIndex(selectedFlowIndex);
            Pair<Map,Map> valuesFromUser2valuesDefinition = ui.getInputsFromUser(flowDefinitionDTO.getFreeInputsFinalNames(),
                    flowDefinitionDTO.getFreeInputTypes(),
                    flowDefinitionDTO.getFreeInputNecessity(),
                    flowDefinitionDTO.getFreeInputUserString());
            if (valuesFromUser2valuesDefinition == null) {
                return;
            }
           engineController.executeFlow(selectedFlowIndex, valuesFromUser2valuesDefinition);
        }
    }

    private void loadingNewXmlFile() {
        boolean flag = true;
        while (flag) {
            String message;
            ui.presentMessageToUser("To go back to main menu, enter a blank string.\n" +
                            "Please enter a full-path to the XML file you desire to load: ");
            String path  = ui.createValidPath(false);
            if (path.isEmpty()) {
                flag = false;
                continue;
            }
            LoadDataDTO loadDataDTO = readXML(path);

            if (!loadDataDTO.getStatus()) {
                message = "Error while loading file: ";
                message = loadDataDTO.getErrorMessage().isEmpty() ? "please check the path given, make sure no hebrew words are included!" : message  + loadDataDTO.getErrorMessage();
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
                    FlowDefinitionDTO flowDefDTO = engineController.getFlowDefinitionDataByIndex(selection-1);

                    if (!flowDefDTO.getStatus()){
                        ui.presentMessageToUser("Error: " + flowDefDTO.getErrorMessage());
                        flag = false;
                    }else {
                       presentDefinition(flowDefDTO);
                    }
                }
            }
        }
    }

    private void presentDefinition(AbstractDTO flowDefDTO) {
        FlowDefinitionDTO flowDefinitionDTO = (FlowDefinitionDTO) flowDefDTO;
        ui.presentMessageToUser("Flow Name: " + flowDefinitionDTO.getFlowName());
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
        List<Pair<String,List<String>>> stepsThatUseFreeInputs = flowDefinitionDTO.getFreeInputs2StepsThatUseThem();
        List<String> freeInputsNecessity = flowDefinitionDTO.getFreeInputNecessity();
        for (int i = 0; i < freeInputs.size(); i++) {
            String name = freeInputs.get(i);
            String presentation = freeInputPresentation(name,
                                                        freeInputTypes.get(i),
                                                        stepsThatUseFreeInputs.get(i).getValue(),
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
        ConsoleApp ctl = new ConsoleApp();
        ctl.start();
    }

    public void presentStatistics(){
        if (engineController.executionOccurred()){
            statisticsManager.collectStatistics();
            ui.presentStatisticsToUser(this.statisticsManager.getFlowStatistics(),this.statisticsManager.getStepStatistics());
        }
        else{
            ui.presentMessageToUser("Error: No Flows Executed Yet!\nreturning to main menu for further actions");
        }

    }

}

