package stepper.flow.execution.logger.flow;

import stepper.flow.execution.logger.AbstractLogger;
import stepper.flow.execution.logger.LogLine;

import java.util.ArrayList;
import java.util.List;

public class FlowExecutionLoggerImpl implements AbstractLogger {
    final private String FlowName;
    final private String FlowId;
    private List<LogLine> logs = new ArrayList<>();
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
