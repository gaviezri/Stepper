package stepper.step.impl;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

public class SpendSomeTimeStep extends AbstractStepDefinition {
    public SpendSomeTimeStep() {
        super("SpendSomeTime", true);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("TIME_TO_SPEND", DataNecessity.MANDATORY,"Total sleeping time (sec)", DataDefinitionRegistry.NUMBER));
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context){
        long timeToSleep = context.getDataValue("TIME_TO_SPEND", Number.class).longValue() * 1000;
        return timeToSleep > 0 ? StepResult.SUCCESS : StepResult.FAILURE;
    }
    @Override
    public StepResult invoke(StepExecutionContext context){

        AbstractLogger logger = context.getStepLogger(this);
        StepResult result = validateInputs(context);
        long timeToSleep = context.getDataValue("TIME_TO_SPEND", Number.class).longValue() * 1000;
        if ( result == StepResult.SUCCESS){

            try {
                logger.addLogLine("About to sleep for " + timeToSleep + " seconds...");
                Thread.sleep(timeToSleep);
                logger.addLogLine("Done sleeping...");
                logger.addSummaryLine("Slept for " + timeToSleep + " seconds, successfully");
            } catch (InterruptedException e) {
                logger.addSummaryLine("Sleeping interrupted");
                result = StepResult.FAILURE;
            }
        }
        else {
            logger.addSummaryLine("Number of seconds to sleep must be greater than 0..." +
                                  " provided value: " +
                                    timeToSleep);
        }
        return result;
    }
}
