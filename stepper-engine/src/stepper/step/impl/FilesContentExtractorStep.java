package stepper.step.impl;

import stepper.flow.execution.context.StepExecutionContext;
import stepper.step.api.AbstractStepDefinition;
import stepper.step.api.enums.StepResult;

public class FilesContentExtractorStep extends AbstractStepDefinition {
    public FilesContentExtractorStep() {
        super("Files Content Extractor", true);
    }

    @Override
    public StepResult invoke(StepExecutionContext context, String finalName) {
        return null;
    }

    @Override
    public StepResult validateInputs(StepExecutionContext context) {
        return null;
    }
}
