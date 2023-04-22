package stepper.step;

import stepper.step.api.StepDefinition;
import stepper.step.impl.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum StepDefinitionRegistry {
    HELLO_WORLD(new HelloWorldStep()),
    PERSON_DETAILS(new PersonDetailsStep()),
    FILE_DUMPER(new FileDumperStep()),
    CSV_EXPORTER(new CSVExporterStep()),
    SPEND_SOME_TIME(new SpendSomeTimeStep()),
    COLLECT_FILES_IN_FOLDER(new CollectFilesInFolderStep()),
    FILES_RENAMER(new FilesRenamerStep()),
    FILES_DELETER(new FilesDeleterStep()),
    PROPERTIES_EXPORTER(new PropertiesExporterStep())
    ;

    private final StepDefinition stepDefinition;

    StepDefinitionRegistry(StepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }

    public static Set<String> getStepNames() {
        return Arrays.stream(StepDefinitionRegistry.values()).sequential()
                .map(StepDefinitionRegistry::getStepDefinition)
                .map(StepDefinition::name)
                .collect(Collectors.toSet());
    }
    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }
}
