package stepper.step;

import stepper.step.api.StepDefinition;
import stepper.step.impl.FileDumperStep;
import stepper.step.impl.HelloWorldStep;
import stepper.step.impl.PersonDetailsStep;

public enum StepDefinitionRegistry {
    HELLO_WORLD(new HelloWorldStep()),
    PERSON_DETAILS(new PersonDetailsStep()),
    FILE_DUMPER(new FileDumperStep())
    ;

    private final StepDefinition stepDefinition;

    StepDefinitionRegistry(StepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }


    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }
}
