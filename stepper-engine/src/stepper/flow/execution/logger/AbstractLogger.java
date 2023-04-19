package stepper.flow.execution.logger;

import java.util.List;

public interface AbstractLogger {

    public void addLogLine(String logLine);
    public void addSummaryLine(String summaryLine);
    public List getLogs();
    public LogLine getSummaryLine();
}
