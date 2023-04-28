package impl;

import api.UIAbstractDefinition;

import java.util.List;
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
    public int getUserNumericResponse(Integer max){
        return getUsersNumericResponse(0,max);
    }
    @Override
    public int getUsersNumericResponse(Integer min, Integer max) {
        int userSelectedOption;

        while(true) {
            try {
                userSelectedOption = Integer.parseInt(scanner.next());
                if (userSelectedOption >= min && userSelectedOption < max) {
                    return userSelectedOption;
                }
            }
//            catch (InputMismatchException e) {}
//            catch (NumberFormatException e)
            catch (Exception e){
                out.println("a non-numeric input was given!\n");
            }
            out.println("Please select a number from the options given!");
        }
    }


    public String createValidPath() {
        return scanner.next().replace("\\","\\\\").replace("/","\\\\");
    }

    @Override
    public void presentLoadedFlowNames(List<String> flowNames) {
        out.println("The following flows are defined in the file:");
        for (int i = 0; i < flowNames.size(); i++){
            out.println( i+1 + ". " + flowNames.get(i));
        }
    }
}
