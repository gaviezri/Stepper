package stepper.flow.execution.logger;

public interface StepExecutionLogger {
    public void addLogLine(String logLine, Object step);
    public void addSummaryLine(String summaryLine, Object step);
}
