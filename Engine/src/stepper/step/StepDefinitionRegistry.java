package stepper.step;

import stepper.step.api.StepDefinition;
import stepper.step.impl.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum StepDefinitionRegistry {
    FILE_DUMPER(new FileDumperStep()),
    CSV_EXPORTER(new CSVExporterStep()),
    SPEND_SOME_TIME(new SpendSomeTimeStep()),
    COLLECT_FILES_IN_FOLDER(new CollectFilesInFolderStep()),
    FILES_RENAMER(new FilesRenamerStep()),
    FILES_DELETER(new FilesDeleterStep()),
    PROPERTIES_EXPORTER(new PropertiesExporterStep()),
    FILES_CONTENT_EXTRACTOR(new FilesContentExtractorStep()),
    ZIPPER(new ZipperStep()),
    COMMAND_LINE(new CommandLineStep()),
    HTTP_CALL(new HTTPCallStep()),
    ;

    private final StepDefinition stepDefinition;

    StepDefinitionRegistry(StepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }

    public static Set<String> getStepNames() {
        return Arrays.stream(StepDefinitionRegistry.values()).sequential()
                .map(StepDefinitionRegistry::getStepDefinition)
                .map(StepDefinition::getStepName)
                .collect(Collectors.toSet());
    }

    public static StepDefinitionRegistry convertFromUserFriendlyToInternal(String stepOriginalName) {
        return valueOf(stepOriginalName.toUpperCase().replace(" ", "_"));
    }

    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }
}
