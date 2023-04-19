package mta.course.java.stepper.step;

import mta.course.java.stepper.step.api.StepDefinition;
import mta.course.java.stepper.step.impl.HelloWorldStep;
import mta.course.java.stepper.step.impl.PersonDetailsStep;

public enum StepDefinitionRegistry {
    HELLO_WORLD(new HelloWorldStep()),
    PERSON_DETAILS(new PersonDetailsStep())
    ;

    private final StepDefinition stepDefinition;

    StepDefinitionRegistry(StepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }


    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }
}
