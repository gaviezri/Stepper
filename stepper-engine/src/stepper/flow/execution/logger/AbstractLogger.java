package stepper.flow.execution.logger;

import java.util.List;

public interface AbstractLogger {

    public void log(String logLine);
    public void addSummaryLine(String summaryLine);
    public List<LogLine> getLogs();
    public LogLine getSummaryLine();
}
