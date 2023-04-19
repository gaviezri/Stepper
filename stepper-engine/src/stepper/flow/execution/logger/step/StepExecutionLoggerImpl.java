package stepper.flow.execution.logger.step;

import stepper.flow.execution.logger.AbstractLogger;
import stepper.flow.execution.logger.LogLine;
import stepper.step.api.StepDefinition;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepExecutionLoggerImpl implements AbstractLogger {
    private List<LogLine> logs = new ArrayList<>();
    private LogLine summaryLine;
    private final StepDefinition step;

    public StepExecutionLoggerImpl(StepDefinition step) {
        this.step = step;
    }

    @Override
    public List<LogLine> getLogs() {
        return logs;
    }

    @Override
    public void addLogLine(String line) {
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
