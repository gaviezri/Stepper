package stepper.flow.execution.logger;

import java.io.Serializable;

public class LogLine implements Serializable {
    final String line;
    final Long timeStamp;

    public LogLine(String logLine) {
        this.line = logLine;
        this.timeStamp = System.currentTimeMillis();
    }

    public String getLine() {
        return line;
    }
    public Long getTimeStamp() {
        return timeStamp;
    }
}