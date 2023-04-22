package stepper.step.impl;

import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.mapping.MappingData;
import stepper.dd.impl.number.NumberData;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilesDeleterStep extends AbstractStepDefinition {
  // TODO: delete this
    public FilesDeleterStep() {
        super("FileDeleter", false);

        //inputs
        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to delete", DataDefinitionRegistry.LIST));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("DELETED_LIST", DataNecessity.NA, "Files failed to be deleted", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("DELETION_STATS", DataNecessity.NA, "Deletion summary results", DataDefinitionRegistry.MAPPING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context, String finalName) {
        context.tick(finalName);

        /**
         * goes through the given list and tries to delete each file
         * then log the actions made and return operation result
         */
        List<File> FILES_LIST = new ArrayList<>(); //context.getDataValue("FILES_LIST",List.class) TODO: the previous command is the correct one but needs to write getDataValue properly
        MappingData DELETION_STATS = new MappingData();
        List<String> DELETED_LIST = new ArrayList<>();
        StepResult res;
        AbstractLogger logger = context.getStepLogger(this);
        boolean filesListIsEmpty =  FILES_LIST.isEmpty();
        int numberOfFilesToDelete = FILES_LIST.size();

        if(!filesListIsEmpty) {
            logger.addLogLine("About to start delete " + numberOfFilesToDelete + " files");

            DELETED_LIST = FILES_LIST.stream()
                    .filter(File::exists)
                    .filter(file -> {
                        boolean deletedSuccessfully = file.delete();
                        if (!deletedSuccessfully) {
                            logger.addLogLine("Failed to delete file " + file.getName());
                        }
                        return !deletedSuccessfully;
                    })
                    .map(File::getName)
                    .collect(Collectors.toList());

            DELETION_STATS.setCar(new NumberData(numberOfFilesToDelete-DELETED_LIST.size()));
            DELETION_STATS.setCdr(new NumberData(DELETED_LIST.size()));
        }

        context.storeDataValue("DELETED_LIST",DELETED_LIST);
        context.storeDataValue("DELETION_STATS",DELETION_STATS);

        if(DELETED_LIST.size() == 0){
            res = StepResult.SUCCESS;
            logger.addSummaryLine(filesListIsEmpty ? "No files? No problem - no files were given so no deletion failed" : "All files deleted successfully!");
        }
        else if(DELETED_LIST.size() != numberOfFilesToDelete){
            res = StepResult.WARNING;
            logger.addLogLine("WARNING: Only " + DELETION_STATS.getCar() + "/" + numberOfFilesToDelete + " files where deleted!");
        }
        else{
            res = StepResult.FAILURE;
            logger.addLogLine("FAILURE: No files where deleted! check yourself before you wreck yourself");
        }
        context.tock(finalName);
        return res;
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return null;
    }
}
