package stepper.flow.execution.logger;

import java.util.List;

public interface StepLogger {

    public void log(String logLine);
    public void addSummaryLine(String summaryLine);
    public List<LogLine> getLogs();
    public LogLine getSummaryLine();
}
