package stepper.flow.execution.archive;

import stepper.flow.execution.FlowExecution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ExecutionArchive {
    private Stack<FlowExecution> flowExecutionStack;

    public ExecutionArchive() {
        flowExecutionStack = new Stack<>();
    }

    public void push(FlowExecution flowExecution) {
        flowExecutionStack.push(flowExecution);
    }

    public FlowExecution pop() {
        return flowExecutionStack.pop();
    }

    public FlowExecution peek() {
        return flowExecutionStack.peek();
    }

    public FlowExecution get(int index) {
        return flowExecutionStack.get(index);
    }

    public boolean executionOccurred() {
        return !flowExecutionStack.isEmpty();
    }

    public List<Map<String, String>> getExecutedFlowHeaders() {
        List<Map<String,String>> headers = new ArrayList<>();
        for (FlowExecution flowExecution : flowExecutionStack) {
            headers.add(flowExecution.getFlowHeader());
        }
        return headers;
    }

    public FlowExecution getFlowExecutionByIndex(int i){
        return flowExecutionStack.get(i);
    }

    public Integer getNumOfFlowExecutionsFromLastFileLoaded(){
        int size = 0;

        for (FlowExecution flowExecution: flowExecutionStack) {
            size += flowExecution.isFromLastFile() ? 1 : 0;
        }

        return size;
    }
}
