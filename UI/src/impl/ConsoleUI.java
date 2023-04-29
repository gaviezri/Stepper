package impl;

import api.UIAbstractDefinition;

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

//    @Override
//    public int getUsersNumericResponse(Integer max) {
//        return 0;
//    }

    @Override
    public int getUsersNumericResponse(Integer max){
        int userSelectedOption;

        while(true) {
            try {
                userSelectedOption = Integer.parseInt(scanner.next());
                if (userSelectedOption >= 0 && userSelectedOption < max) {
                    return userSelectedOption;
                }
            }catch (Exception e){
                out.println("a non-numeric input was given!\n");
            }
            out.println("Please select a number from the options given!");
        }
    }


//    @Override
//    public int getUsersNumericResponse(Integer min, Integer max) {
//        int userSelectedOption;
//
//        while(true) {
//            try {
//                userSelectedOption = Integer.parseInt(scanner.next());
//                if (userSelectedOption >= min && userSelectedOption < max) {
//                    return userSelectedOption;
//                }
//            }
////            catch (InputMismatchException e) {}
////            catch (NumberFormatException e)
//            catch (Exception e){
//                out.println("a non-numeric input was given!\n");
//            }
//            out.println("Please select a number from the options given!");
//        }
//    }


    public String createValidPath() {
        return scanner.next().replace("\\","\\\\").replace("/","\\\\");
    }
    @Override
    public void presentLoadedFlowNames(List<String> flowNames) {
        out.println("This are the flows currently loaded in the system\n" +
                "please choose one by entering the corresponding number:");
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
        Integer userResponse;
        presentLoadedFlowNames(flowNames);
        presentBackToMainMenuOption();
        userResponse = getUsersNumericResponse(flowNames.size());
        return userResponse-1;
    }

    @Override
    public void presentBackToMainMenuOption() {
        out.println("0. Back to main menu");
    }

    @Override
    public Map<String, Object> getInputsFromUser(List<String> freeInputsFinalNames, List<String> freeInputTypes, List<String> freeInputNecessity) {
        String userInput;
        out.println("Please enter the following inputs.\nThe inputs will be presented one by one, after filling the" +
                " corresponding data press enter for the next one to appear.\n");
        for(int i = 0; i < freeInputsFinalNames.size(); ++i){
            out.print(freeInputsFinalNames.get(i)+ ": ");
            userInput = scanner.next();
            //TODO: check type and parse if not string. then add to res MAP.
        }
        return null;
    }
}

