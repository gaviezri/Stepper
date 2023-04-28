package stepper.flow.definition.mapping;

import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.flow.definition.aliasing.manager.DataAliasingManager;
import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.step.api.enums.DataNecessity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;

public class MappingGraph {
    private final List<String> stepNodes;
    private List<List<MappingEdge>> edges;

    public MappingGraph(List<StepUsageDeclaration> stepUsageDecl, List<Pair<String, String>> customMapping, DataAliasingManager dataAliasingManager) {
        // creating the custom mapping in the graph's constructor

        this.stepNodes = stepUsageDecl.stream()
                .map(StepUsageDeclaration::getFinalStepName)
                .collect(Collectors.toList());

        edges = new ArrayList<>();
        range(0, stepNodes.size()).forEach(i -> edges.add(new ArrayList<>()));

        for (Pair<String, String> pair : customMapping) {
            String[] source_data = pair.getKey().split(":");
            String[] target_data = pair.getValue().split(":");

            int sourceNodeIdx = this.stepNodes.indexOf(source_data[0]);
            int targetNodeIdx = this.stepNodes.indexOf(target_data[0]);

            // stepUsageDecl.get(sourceNodeIdx).getStepDefinition();

            String sourceDataOriginalName = dataAliasingManager.getOriginalDataName(source_data[0], source_data[1]);
            String targetDataOriginalName = dataAliasingManager.getOriginalDataName(target_data[0], target_data[1]);
            DataDefinition sourceDataDef = stepUsageDecl.get(sourceNodeIdx).getStepDefinition().getResourceDataDefinition(sourceDataOriginalName);
            DataDefinition targetDataDef = stepUsageDecl.get(targetNodeIdx).getStepDefinition().getResourceDataDefinition(targetDataOriginalName);
            DataNecessity targetDataNecessity = stepUsageDecl.get(targetNodeIdx).getStepDefinition().getResourceNecessity(targetDataOriginalName);

            validateCustomMapping(source_data, target_data, sourceNodeIdx, targetNodeIdx, sourceDataOriginalName, targetDataOriginalName, sourceDataDef, targetDataDef);
            edges.get(sourceNodeIdx).add(new MappingEdge(source_data[0], source_data[1],
                                                   target_data[0], target_data[1], sourceDataDef, targetDataNecessity));

        }
    }

    public List<String> getStepNodes() {
        return stepNodes;
    }
    private static void validateCustomMapping(String[] source_data, String[] target_data, int sourceNodeIdx, int targetNodeIdx, String sourceDataOriginalName, String targetDataOriginalName, DataDefinition sourceDataDef, DataDefinition targetDataDef) {
        if (sourceDataDef == null || targetDataDef == null) {
            throw new RuntimeException("Data definition not found for " + sourceDataOriginalName + " or " + targetDataOriginalName);
        } else if (!sourceDataDef.getType().equals(targetDataDef.getType())) {
            throw new RuntimeException("Mapping Attempt Fail! Trying to map " + source_data[0] + " Output: " + sourceDataOriginalName +
                    " to " + target_data[0] + "Input:" + targetDataOriginalName + "\n But they are not of the same type!" + "\n" +
                    "Source Type: " + sourceDataDef.getType() + "\n" +
                    "Target Type: " + targetDataDef.getType());
        } else if (targetNodeIdx <= sourceNodeIdx){
            throw new RuntimeException("Mapping Attempt Fail! Trying to map " + source_data[0] + " Output: " + sourceDataOriginalName +
                    " to " + target_data[0] + "Input:" + targetDataOriginalName + "\n But the target step executes before the source step!");
        }
    }

    public void createAutomaticMapping(FlowDefinition flowDefinition) {
        // iterate over all the nodes which have node after them
        for (int nodeSrcIdx = 0; nodeSrcIdx < stepNodes.size() - 1; nodeSrcIdx++) {
            // iterate over the nodes after the current node
            for (int nodeTrgtIdx = nodeSrcIdx + 1 ; nodeTrgtIdx < stepNodes.size(); nodeTrgtIdx++) {
                // get source outputs and target inputs
                mappingAttempt(flowDefinition, nodeSrcIdx, nodeTrgtIdx);

            }
        }

    }

    private void mappingAttempt(FlowDefinition flowDefinition, int nodeSrcIdx, int nodeTrgtIdx) {

        String sourceFinalStepName = stepNodes.get(nodeSrcIdx);
        String targetFinalStepName = stepNodes.get(nodeTrgtIdx);
        List<String> sourceOutputsOriginalNames = flowDefinition.getStepOutputsOriginalNames(sourceFinalStepName);
        List<String> targetInputsOriginalNames = flowDefinition.getStepInputsOriginalNames(targetFinalStepName);
        List<String> sourceOutputs = flowDefinition.getStepOutputsFinalNames(sourceFinalStepName);
        List<String> targetInputs = flowDefinition.getStepInputsFinalNames(targetFinalStepName);
        // if they match by name and definition type - add an edge
        for (int srcOutputIdx = 0; srcOutputIdx < sourceOutputs.size(); srcOutputIdx++) {
            for (int trgtInputIdx = 0; trgtInputIdx < targetInputs.size(); trgtInputIdx++) {

                if (inputIsNotSatisfied(targetFinalStepName, sourceFinalStepName)) {
                    // if they differ by type - throw an exception
                    if (sourceOutputs.get(srcOutputIdx).equals(targetInputs.get(trgtInputIdx))) {

                        dataTypeValidation(flowDefinition, sourceFinalStepName, targetFinalStepName,
                                sourceOutputsOriginalNames, targetInputsOriginalNames,
                                sourceOutputs, targetInputs, srcOutputIdx, trgtInputIdx);

                        DataDefinition sourceDataDef = flowDefinition.getResourceDataDefinition(sourceFinalStepName, sourceOutputsOriginalNames.get(srcOutputIdx));
                        DataNecessity targetDataNecessity = flowDefinition.getResourceDataNecessity(targetFinalStepName, targetInputsOriginalNames.get(trgtInputIdx));

                        edges.get(nodeSrcIdx).add(new MappingEdge(sourceFinalStepName, sourceOutputs.get(srcOutputIdx),
                                targetFinalStepName, targetInputs.get(trgtInputIdx), sourceDataDef, targetDataNecessity));
                    }
                }
            }
        }
    }

    private boolean inputIsNotSatisfied(String targetFinalStepName, String TargetFinalInputName) {
        for ( List<MappingEdge> edges : this.edges) {
            for (MappingEdge edge : edges) {
                if (edge.getTargetStepName().equals(targetFinalStepName) &&
                        edge.getTargetDataName().equals(TargetFinalInputName)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void dataTypeValidation(FlowDefinition flowDefinition, String sourceFinalStepName, String targetFinalStepName, List<String> sourceOutputsOriginalNames, List<String> targetInputsOriginalNames, List<String> sourceOutputs, List<String> targetInputs, int srcOutputIdx, int trgtInputIdx) {
        Class srcOutputClass = flowDefinition.getResourceDataDefinition(sourceFinalStepName, sourceOutputsOriginalNames.get(srcOutputIdx)).getType();
        Class trgtInputClass = flowDefinition.getResourceDataDefinition(targetFinalStepName, targetInputsOriginalNames.get(trgtInputIdx)).getType();
        if (!srcOutputClass.equals(trgtInputClass)) {
            throw new RuntimeException("Mapping Attempt Fail! Trying to map " + sourceFinalStepName + " Output: " + sourceOutputs.get(srcOutputIdx) +
                    " to " + targetFinalStepName + "Input:" + targetInputs.get(trgtInputIdx) + "\n But they are not of the same type!" + "\n" +
                    "Source Type: " + srcOutputClass + "\n" +
                    "Target Type: " + trgtInputClass);
        }
    }

    public void validateMandatoryInputs() {
        // there are mandatory inputs which are not user-friendly - TODO
        // numerous mandatory inputs with the same name from different types! - TODO
    }

    public void addEdge(MappingEdge edge) {
        int sourceNodeIdx = this.stepNodes.indexOf(edge.getSourceStepName());
        edges.get(sourceNodeIdx).add(edge);
    }

    public boolean isSatisfied(String name) {
        for (List<MappingEdge> edges : this.edges) {
            for (MappingEdge edge : edges) {
                if (edge.getTargetDataName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
}
