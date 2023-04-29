package impl;

import api.UIAbstractDefinition;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;

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
    public String createValidPath() {
        String path =  scanner.nextLine();
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
        return getUsersNumericResponse(flowNames.size()) - 1;
    }

    @Override
    public void presentBackToMainMenuOption() {
        out.println("0. Back to main menu");
    }

    @Override
    public Pair<Map,Map> getInputsFromUser(List<String> freeInputsFinalNames, List<String> freeInputTypes, List<String> freeInputNecessity) {
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

        out.println("The inputs will be presented one by one.\n after filling the" +
                " corresponding data press enter for the next one to appear.\n");
        out.println("Mandatory inputs:");
        getMandatoryInputs(valueResult, typeResult, mandatoryInputs, mandatoryInputsTypes);
        out.println("Optional inputs:");
        getOptionalInputs(valueResult, typeResult, optionalInputs, optionalInputsTypes);
        return new Pair<>(valueResult, typeResult);
    }

    private void getOptionalInputs(Map<String, Object> valueRes, Map<String,String> typeRes, List<String> optionalInputs, List<String> optionalInputsTypes) {
        out.println("If you wish to skip an optional input, just press enter without entering any data.");
        for(int i = 0; i < optionalInputs.size(); ++i){
            if(valueRes.containsKey(optionalInputs.get(i))){
                continue;
            }
            out.print(optionalInputs.get(i)+ ": ");
            String userInput = scanner.nextLine();
            if (userInput.equals("")){
                continue;
            }
            if (optionalInputsTypes.get(i).equals("Integer")){
                try{
                    valueRes.put(optionalInputs.get(i), Integer.parseInt(userInput));
                    typeRes.put(optionalInputs.get(i), "Integer");
                } catch (Exception e){
                    out.println("The input you entered is not an integer, please try again.");
                    --i;
                }
            } else if (optionalInputsTypes.get(i).equals("String")){
                valueRes.put(optionalInputs.get(i), userInput);
                typeRes.put(optionalInputs.get(i), "String");
            } else if (optionalInputsTypes.get(i).equals("Path")){
                valueRes.put(optionalInputs.get(i), createValidPath(userInput));
                typeRes.put(optionalInputs.get(i), "Path");
            }
        }
    }

    private void getMandatoryInputs(Map<String, Object> valueRes, Map<String,String> typeRes,List<String> mandatoryInputs, List<String> mandatoryInputsTypes) {
        for(int i = 0; i < mandatoryInputs.size(); ++i){
            if(valueRes.containsKey(mandatoryInputs.get(i))){
                continue;
            }
            out.print(mandatoryInputs.get(i)+ ": ");
            String userInput = scanner.nextLine();
            if (userInput.equals("")){
                out.println("This input is mandatory, please enter a valid input.");
                --i;
                continue;
            }
            if (mandatoryInputsTypes.get(i).equals("Integer")){
                try{
                    valueRes.put(mandatoryInputs.get(i), Integer.parseInt(userInput));
                    typeRes.put(mandatoryInputs.get(i), "Integer");
                } catch (Exception e){
                    out.println("The input you entered is not an integer, please try again.");
                    --i;
                }
            } else if (mandatoryInputsTypes.get(i).equals("String")){
                valueRes.put(mandatoryInputs.get(i), userInput);
                typeRes.put(mandatoryInputs.get(i), "String");
            } else if (mandatoryInputsTypes.get(i).equals("Path")){
                valueRes.put(mandatoryInputs.get(i), createValidPath(userInput));
                typeRes.put(mandatoryInputs.get(i), "Path");
            }
        }
    }
}
