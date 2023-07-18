package stepper.flow.execution.logger.step;

import stepper.flow.execution.logger.StepLogger;
import stepper.flow.execution.logger.LogLine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StepExecutionLoggerImpl implements StepLogger, Serializable {
    private final List<LogLine> logs = new ArrayList<>();
    private LogLine summaryLine;
    private final String step;

    public StepExecutionLoggerImpl(String step) {
        this.step = step;
    }

    @Override
    public List<LogLine> getLogs() {
        return logs;
    }

    @Override
    public void log(String line) {
        logs.add(new LogLine(line));
    }
    @Override
    public void addSummaryLine(String line) {
        summaryLine = new LogLine(line);
    }

    @Override
    public LogLine getSummaryLine() {
        return summaryLine;
    }
}
