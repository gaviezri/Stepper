package stepper.step.impl;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.exception.GivenValueTypeDontMatchException;
import stepper.exception.NoMatchingKeyWasFoundException;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.StepLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class CommandLineStep extends AbstractStepDefinition {

    public CommandLineStep() {
        super("Command Line", false);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("COMMAND", DataNecessity.MANDATORY, "Command", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("ARGUMENTS", DataNecessity.OPTIONAL, "Command arguments", DataDefinitionRegistry.LIST));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Command output", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.tick();
        StepLogger logger = context.getStepLogger();
        String command = null;
        List<String> arguments = null;
        try {
            command = context.getDataValue("COMMAND", String.class);
        } catch (GivenValueTypeDontMatchException e) {
            logger.log("Error: command is not a string");
        } catch (NoMatchingKeyWasFoundException e) {
            logger.log("Error: command not found in context");
        } catch (Exception e) {
            logger.log(String.format("Error getting command from context: %s", e.getMessage()));
        }
        try {
            arguments = context.getDataValue("ARGUMENTS", List.class);
        } catch (Exception e) {
            logger.log("Problem encountered while getting arguments from context: " + e.getMessage());
        }
        String msg;
        ProcessBuilder pb = new ProcessBuilder(command);
//        pb.directory(new File("C:\\Users\\a\\Downloads\\stepper utils\\test"));
        if (arguments != null) {
            pb.command().addAll(arguments);
            msg = "About to invoke " + command + arguments;
        } else {
            msg = "About to invoke " + command;
        }
        try {
            logger.log(msg);
            Process p = pb.start();
//            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            logger.addSummaryLine("Command executed successfully");
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            context.storeDataValue("RESULT", sb.toString(), DataDefinitionRegistry.STRING);
        } catch (Exception e) {
            logger.log("Error executing command: " + e.getMessage());
            logger.addSummaryLine("Command was not executed successfully");
            context.storeDataValue("RESULT", e.getMessage(), DataDefinitionRegistry.STRING);
        }
        context.getCurrentStepManager().setStepResult(StepResult.SUCCESS);
        return StepResult.SUCCESS;
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return null;
    }

    @Override
    public stepper.dd.api.DataDefinition getResourceDataDefinition(String dataOriginalName) {
        switch (dataOriginalName) {
            case "COMMAND":
            case "RESULT":
                return DataDefinitionRegistry.STRING;
            case "ARGUMENTS":
                return DataDefinitionRegistry.LIST;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }

    @Override
    public DataNecessity getResourceNecessity(String dataOriginalName) {
        switch (dataOriginalName) {
            case "COMMAND":
                return DataNecessity.MANDATORY;
            case "RESULT":
                return DataNecessity.NA;
            case "ARGUMENTS":
                return DataNecessity.OPTIONAL;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }
}
