package stepper.main;

import stepper.dd.api.DataDefinition;
import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.definition.api.FlowDefinitionImpl;
import stepper.flow.definition.api.StepUsageDeclarationImpl;
import stepper.flow.execution.FlowExecution;
import stepper.flow.execution.runner.FlowExecutor;
import stepper.flow.loader.FlowLoader;
import stepper.step.StepDefinitionRegistry;
import stepper.step.api.DataDefinitionDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main {
    public static UUID total_flows = UUID.randomUUID();
    public static void main(String[] args) {


        List<FlowDefinition> flowDefinitions = new ArrayList<>();
        FlowLoader flowLoader = new FlowLoader();
        try {
            flowDefinitions = flowLoader.loadFlowFromXML(flowLoader.validateFilePath(""));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        for (FlowDefinition flowDefinition : flowDefinitions) {
            // request missing inputs from user

            }
            FlowExecutor flowExecutor = new FlowExecutor();
            flowExecutor.executeFlow(new FlowExecution(total_flows.toString()));
            total_flows++;
        }


    }
}
