package stepper.flow.execution.archive;

import stepper.flow.execution.FlowExecution;

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

}
