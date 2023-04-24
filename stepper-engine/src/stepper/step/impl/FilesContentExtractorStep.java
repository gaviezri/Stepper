package stepper.step.impl;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.relation.RelationData;
import stepper.exception.GivenValueTypeDontMatchException;
import stepper.exception.NoMatchingKeyWasFoundException;
import stepper.flow.execution.context.StepExecutionContext;
import stepper.flow.execution.logger.AbstractLogger;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.DataDefinitionDeclarationImpl;
import stepper.step.api.enums.DataNecessity;
import stepper.step.api.enums.StepResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FilesContentExtractorStep extends AbstractStepDefinition {
    private final List<String> COLUMNS_TITLES = new ArrayList<>(3);

    public FilesContentExtractorStep() {
        super("File content extractor", true);

        COLUMNS_TITLES.add("Num.");
        COLUMNS_TITLES.add("File Name");
        COLUMNS_TITLES.add("Data in given line");

        //inputs
        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to extract", DataDefinitionRegistry.LIST));
        addInput(new DataDefinitionDeclarationImpl("LINE", DataNecessity.MANDATORY, "Line number to extract", DataDefinitionRegistry.NUMBER));

        //outputs
        addOutput(new DataDefinitionDeclarationImpl("DATA", DataNecessity.NA, "Data extraction", DataDefinitionRegistry.RELATION));
    }
    private String getLineByRowNumber(Integer rowNum, File file) throws IOException {
        try(Stream<String> lines = Files.lines(file.toPath())){
            return lines.skip(rowNum-1).findFirst().get();
        }
    }

    private RelationData createFilesContentRelation(List<File> filesList,AbstractLogger logger, Integer lineNumberToExtract){
        File curFile;
        String line;
        RelationData data = new RelationData(COLUMNS_TITLES);

        /*
         * for every File object in the input list:
         * add new row with serial number, file name and wanted line.
         * if file don't exist or no read permissions -> a row with serial number and "File not found" text in the rest
         *  of the columns
         * if lines number in file is less than the given row number -> a row with serial number, file name and "No such
         *  line" text will be added
         */
        for (int i = 1; i < filesList.size(); ++i) {
            List<String> row = new ArrayList<>();
            curFile = filesList.get(i - 1);
            row.add(String.valueOf(i));

            if(curFile.exists()) {
                row.add(curFile.getName());
                try {
                    line = getLineByRowNumber(lineNumberToExtract, curFile);
                    row.add(line.isEmpty() ? "Not such line" : line);
                } catch (SecurityException e) {
                    logger.addLogLine("Problem extracting line number " + i + "from file " + curFile.getName() + "-> no read access to the file");
                    row.add("File not found");
                } catch (IOException e) {
                    logger.addLogLine("An an I/O error occurs opening file  " + curFile.getName());
                }
            }
            else {
                logger.addLogLine("Problem extracting line number " + i + "from file " + curFile.getName() + "file dont exists!");
                        row.add("File not found");
                row.add("File not found");
            }

            data.addRow(row);
        }

        return data;

    }

    @Override
    public StepResult invoke(StepExecutionContext context, String finalName) {
        context.tick(finalName);
        AbstractLogger logger = context.getStepLogger(this);

        try {
            List<File> filesList = context.getDataValue("FILES_LIST",List.class);
            Integer lineNumberToExtract = context.getDataValue("LINE", Integer.class);

            context.storeDataValue("DATA",createFilesContentRelation(filesList,logger,lineNumberToExtract));

            if(filesList.isEmpty()) {
                logger.addSummaryLine("No files were given!");
            }
            context.tock(finalName);
            return StepResult.SUCCESS;
        }
        catch(GivenValueTypeDontMatchException e){
            logger.addLogLine(e.getMessage());

        }
        catch(NoMatchingKeyWasFoundException e){

        }

    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return null;
    }
}
