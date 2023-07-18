package stepper.flow.execution.logger;

import java.util.List;

public interface StepLogger {

    void log(String logLine);
    void addSummaryLine(String summaryLine);
    List<LogLine> getLogs();
    LogLine getSummaryLine();
}
