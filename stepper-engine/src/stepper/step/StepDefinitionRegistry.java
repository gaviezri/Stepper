package stepper.step;

import stepper.step.api.StepDefinition;
import stepper.step.impl.*;

public enum StepDefinitionRegistry {
    HELLO_WORLD(new HelloWorldStep()),
    PERSON_DETAILS(new PersonDetailsStep()),
    FILE_DUMPER(new FileDumperStep()),
    CSV_EXPORTER(new CSVExporterStep()),
    SPEND_SOME_TIME(new SpendSomeTimeStep()),
    COLLECT_FILES_IN_FOLDER(new CollectFilesInFolderStep()),
    FILES_RENAMER(new FilesRenamerStep())
    ;

    private final StepDefinition stepDefinition;

    StepDefinitionRegistry(StepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }


    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }
}
