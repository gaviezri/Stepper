package impl;

import api.UIAbstractDefinition;
import javafx.util.Pair;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.out;

public class ConsoleUI extends UIAbstractDefinition {
    private final Scanner scanner = new Scanner(System.in);
    private List<String> currentMenuData;


    private void setCurrentMenuData(List<String> curMenu){
        currentMenuData = curMenu;
    }
    @Override
    public void PresentMainMenu() {
        setCurrentMenuData(this.menuData.getMainMenu());
        for (String line:currentMenuData
             ) {
            out.println(line);
        }
    }
    @Override
    public int getUsersNumericResponse(Integer min, Integer max) {
        int userSelectedOption;

        while (true) {
                try {
                userSelectedOption = Integer.parseInt(scanner.nextLine());
                if (userSelectedOption >= min && userSelectedOption <= max) {
                    return userSelectedOption;
                }
            } catch (Exception e) {
                out.println("a non-numeric input was given!\n");
            }
            out.println("Please select a number from the options given!");
        }
    }

    @Override
    public int getUsersNumericResponse(Integer max){
        return getUsersNumericResponse(1,max);
    }



    public String createValidPath(String path) {
        return path.replace("\\","\\\\").replace("/","\\\\").replace("\"","");
    }
    public String createValidPath(Boolean isDir) {
        String path =  scanner.nextLine();
        if (isDir) {
           while(path.lastIndexOf('\\') < path.lastIndexOf('.')){
                out.println("Please enter a valid path to a directory!");
                path = scanner.nextLine();
           }

        }
        return path.replace("\\","\\\\").replace("/","\\\\").replace("\"","");
    }
    @Override
    public void presentLoadedFlowNames(List<String> flowNames) {
        out.println("The following are the flows that are currently loaded in the system.\n" +
                "Please choose by entering the corresponding number:");
        for (int i = 0; i < flowNames.size(); i++){
            out.println( i+1 + ". " + flowNames.get(i));
        }
    }
    @Override
    public void presentMessageToUser(String message) {
        out.println(message);
    }

    @Override
    public Integer getSelectedFlowIndexFromUser(List<String> flowNames) {
        presentLoadedFlowNames(flowNames);
        presentBackToMainMenuOption();
        return getUsersNumericResponse(0,flowNames.size()) - 1;
    }

    @Override
    public void presentStatisticsToUser(Map<String, Pair<Integer, Duration>> flowStatistics, Map<String, Pair<Integer, Duration>> stepStatistics) {
        out.println("Statistics for flows from recent loaded files that had occurred so far:\n");
        out.println("FLOWS STATISTICS:\n");
        out.println("-----------------\n");

        for (String flowName:flowStatistics.keySet()){
            printStatisticsLineToConsole("Flow",flowName,flowStatistics.get(flowName).getKey(),flowStatistics.get(flowName).getValue());
        }
        out.println("\nSTEPS STATISTICS:\n");
        out.println("-----------------\n");

        for (String stepName:stepStatistics.keySet()){
            printStatisticsLineToConsole("Step",stepName,stepStatistics.get(stepName).getKey(),stepStatistics.get(stepName).getValue());
        }
        out.println("\n");
    }

    private void printStatisticsLineToConsole(String typeName, String name, int occurrencesCounter, Duration totalDuration){
        out.printf("%s name: %s, number of executions: %d , average duration: %d ms\n%n",
                typeName,name,occurrencesCounter,totalDuration.toMillis()/occurrencesCounter);
    }

    @Override
    public void presentBackToMainMenuOption() {
        out.println("0. Back to main menu");
    }

    @Override
    public Pair<Map,Map> getInputsFromUser(List<String> freeInputsFinalNames, List<String> freeInputTypes, List<String> freeInputNecessity, List<String> freeInputUserStrings) {
        Map<String, Object> valueResult = new java.util.HashMap<>();
        Map<String, String> typeResult = new java.util.HashMap<>();
        List<Boolean> mandatoryInputsMask = freeInputNecessity.stream()
                        .map(x -> x.equals("MANDATORY"))
                        .collect(java.util.stream.Collectors.toList());

        List<String> mandatoryInputs = freeInputsFinalNames.stream()
                        .filter(x -> mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                        .collect(java.util.stream.Collectors.toList());

        List<String> optionalInputs = freeInputsFinalNames.stream()
                        .filter(x -> !mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                        .collect(java.util.stream.Collectors.toList());

        List<String> mandatoryInputsTypes = freeInputsFinalNames.stream()
                        .filter(x -> mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                        .map(x -> freeInputTypes.get(freeInputsFinalNames.indexOf(x)))
                        .collect(java.util.stream.Collectors.toList());

        List<String> optionalInputsTypes = freeInputsFinalNames.stream()
                        .filter(x -> !mandatoryInputsMask.get(freeInputsFinalNames.indexOf(x)))
                        .map(x -> freeInputTypes.get(freeInputsFinalNames.indexOf(x)))
                        .collect(java.util.stream.Collectors.toList());

        List<String> mandatoryInputsUserString = freeInputUserStrings.stream()
                        .filter(x -> mandatoryInputsMask.get(freeInputUserStrings.indexOf(x)))
                        .collect(java.util.stream.Collectors.toList());

        List<String> optionalInputsUserString = freeInputUserStrings.stream()
                        .filter(x -> !mandatoryInputsMask.get(freeInputUserStrings.indexOf(x)))
                        .collect(java.util.stream.Collectors.toList());

        out.println("The inputs will be presented one by one.\n after filling the" +
                " corresponding data press enter for the next one to appear.\nTo exit the flow creation process enter \"EXIT\".");
        try {
            out.println("Mandatory inputs\n-----------------");
            getFreeInputs(valueResult, typeResult, mandatoryInputs, mandatoryInputsTypes, mandatoryInputsUserString, true);
            out.println("Optional inputs\n-----------------");
            getFreeInputs(valueResult, typeResult, optionalInputs, optionalInputsTypes,  optionalInputsUserString, false);
        } catch (Exception e){
            return null;
        }
        return new Pair<>(valueResult, typeResult);
    }

    private void getFreeInputs(Map<String, Object> valueRes, Map<String,String> typeRes, List<String> Inputs, List<String> InputsTypes, List<String> InputsUserString, Boolean isMandatory) throws Exception {
        if (!isMandatory){
            out.println("If you wish to skip an optional input, just press enter without entering any data.");
        }

        for(int i = 0; i < Inputs.size(); ++i){

            if(valueRes.containsKey(Inputs.get(i))){
                continue;
            }
            out.print("(" + Inputs.get(i) + ") " + InputsUserString.get(i)+ ": ");
            String userInput = scanner.nextLine();
            if (userInput.equals("") && isMandatory){
                out.println("This input is mandatory, please enter a valid input.");
                --i;
                continue;
            }
            if(userInput.equals("EXIT")){
                throw new Exception();
            }
            if (userInput.equals("")){
                continue;
            }
            if (InputsTypes.get(i).equals("Double")){
                try{
                    valueRes.put(Inputs.get(i), Double.parseDouble(userInput));
                    typeRes.put(Inputs.get(i), "Double");
                } catch (Exception e){
                    out.println("The input you entered is not an Double, please try again.");
                    --i;
                }
            }
            else if (InputsTypes.get(i).equals("Integer")){
                try{
                    valueRes.put(Inputs.get(i), Integer.parseInt(userInput));
                    typeRes.put(Inputs.get(i), "Number");
                } catch (Exception e){
                    out.println("The input you entered is not an Integer, please try again.");
                    --i;
                }
            }
            else if (InputsTypes.get(i).equals("String")){
                valueRes.put(Inputs.get(i), userInput);
                typeRes.put(Inputs.get(i), "String");
            }
            else if (InputsTypes.get(i).equals("Path")){
                valueRes.put(Inputs.get(i), createValidPath(userInput));
                typeRes.put(Inputs.get(i), "Path");
            }
        }
        out.println("-----------------");
    }
    @Override
    public void printResource(String name, String value, String type){
        presentMessageToUser("\nName: " + name);
        presentMessageToUser("Value: " + value);
        presentMessageToUser("Type: " + type + "\n");
    }
}
