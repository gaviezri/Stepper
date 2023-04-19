package stepper.flow.execution.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepExecutionLoggerImpl implements StepExecutionLogger{
    private List<String> log = new ArrayList<String>();

    public StepExecutionLoggerImpl() {

    }

    public List<String> getLog() {
        return log;
    }

    private Map getLogSignature(Object step){
        Map logSignature = new HashMap();
        logSignature.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        logSignature.put("stepName", step.getClass().getSimpleName());
        return  logSignature;

    }


    public void addLogLine(String logLine, Object step) {
        Map logSig = getLogSignature(step);
        log.add("epoch: " + logSig.get("timeStamp") + " @" + logSig.get("stepName") + ": " + logLine);
    }

    public void addSummaryLine(String summaryLine, Object step) {
        Map logSig = getLogSignature(step);
        log.add("~~~SUMMARY~~~[" + logSig.get("stepName") + "]\n" +
                "epoch: " + logSig.get("timeStamp") + " @" + logSig.get("stepName")  + summaryLine);
    }
}
