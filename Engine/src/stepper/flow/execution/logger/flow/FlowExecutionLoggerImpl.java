package stepper.flow.execution.logger.flow;

import stepper.flow.execution.logger.StepLogger;
import stepper.flow.execution.logger.LogLine;

import java.util.ArrayList;
import java.util.List;

public class FlowExecutionLoggerImpl implements StepLogger {
    final private String FlowName;
    final private String FlowId;
    private final List<LogLine> logs = new ArrayList<>();
    private LogLine summaryLine;

    public FlowExecutionLoggerImpl(String flowName, String flowId) {
        FlowName = flowName;
        FlowId = flowId;
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
