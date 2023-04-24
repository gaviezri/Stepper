package stepper.step.impl;

import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.StepDefinitionRegistry;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.StepDefinition;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

public class SpendSomeTimeStep extends AbstractStepDefinition {
    public SpendSomeTimeStep() {
        super("Spend Some Time", true);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("TIME_TO_SPEND", DataNecessity.MANDATORY,"Total sleeping time (sec)", DataDefinitionRegistry.NUMBER));
    }
    @Override
    public DataNecessity getResourceNecessity(String dataOriginalName) {
        switch (dataOriginalName) {
            case "TIME_TO_SPEND":
                return DataNecessity.MANDATORY;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }
    @Override
    public DataDefinition getResourceDataDefinition(String dataOriginalName) {
        switch (dataOriginalName) {
            case "TIME_TO_SPEND":
                return DataDefinitionRegistry.NUMBER;
            default:
                throw new RuntimeException("Unknown data name: " + dataOriginalName + " for step: " + getStepName());
        }
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context){
        try {
            long timeToSleep = context.getDataValue("TIME_TO_SPEND", Number.class).longValue() * 1000;
            return timeToSleep > 0 ? StepResult.SUCCESS : StepResult.FAILURE;
        } catch (Exception e) {
            context.getStepLogger(StepDefinitionRegistry.valueOf(context.getCurrentStepName()).getStepDefinition())
                    .addSummaryLine("Error while fetching TIME_TO_SPEND from context: " + e.getMessage());
            return StepResult.FAILURE;
        }
    }
    @Override
    public StepResult invoke(StepExecutionContext context, String finalName){
        context.tick(finalName);
        AbstractLogger logger = context.getStepLogger(this);
        StepResult result = validateInputs(context);
        long timeToSleep = -1;
        if ( result == StepResult.SUCCESS){

            try {
                timeToSleep = context.getDataValue("TIME_TO_SPEND", Number.class).longValue() * 1000;
                logger.addLogLine("About to sleep for " + timeToSleep + " seconds...");
                Thread.sleep(timeToSleep);
                logger.addLogLine("Done sleeping...");
                logger.addSummaryLine("Slept for " + timeToSleep + " seconds, successfully");
            } catch (Exception e) {
                logger.addSummaryLine("Sleeping interrupted:" + e.getMessage());
                result = StepResult.FAILURE;
            }
        }
        else {
            logger.addSummaryLine("Number of seconds to sleep must be greater than 0..." +
                                  " provided value: " +
                                    timeToSleep);
        }
        context.tock(finalName);
        return result;
    }
}
