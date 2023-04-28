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
    public int getUsersNumericResponse() {
        int userSelectedOption;

        while(true) {
            try {
                userSelectedOption = Integer.parseInt(scanner.next());
                if (userSelectedOption >= 0 && userSelectedOption < currentMenuData.size()) {
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
        out.println("Please enter a full-path to the XML file you desire to load: ");
        return scanner.next().replace("\\","\\\\").replace("/","\\\\");
    }

    @Override
    public void notifyUser(String message){
        out.println(message);
    }
}
