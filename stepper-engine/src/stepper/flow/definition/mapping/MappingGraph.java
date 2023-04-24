package stepper.flow.definition.mapping;

import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.flow.definition.aliasing.manager.DataAliasingManager;
import stepper.flow.definition.api.StepUsageDeclaration;
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

            stepUsageDecl.get(sourceNodeIdx).getStepDefinition();

            String sourceDataOriginalName = dataAliasingManager.getOriginalDataName(source_data[0], source_data[1]);
            String targetDataOriginalName = dataAliasingManager.getOriginalDataName(target_data[0], target_data[1]);
            DataDefinition sourceDataDef = stepUsageDecl.get(sourceNodeIdx).getStepDefinition().getResourceDataDefinition(sourceDataOriginalName);
            DataDefinition targetDataDef = stepUsageDecl.get(targetNodeIdx).getStepDefinition().getResourceDataDefinition(targetDataOriginalName);

            validateCustomMapping(source_data, target_data, sourceNodeIdx, targetNodeIdx, sourceDataOriginalName, targetDataOriginalName, sourceDataDef, targetDataDef);
            edges.get(sourceNodeIdx).add(new MappingEdge(source_data[0], source_data[1],
                                                   target_data[0], target_data[1], sourceDataDef));

        }
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

    public void createAutomaticMapping() {
        // TODO

    }
    public void validateMandatoryInputs() {
        // there are mandatory inputs which are not user-friendly - TODO
        // numerous mandatory inputs with the same name from different types! - TODO
    }

    public void addEdge(MappingEdge edge) {
        int sourceNodeIdx = this.stepNodes.indexOf(edge.getSourceStepName());
        edges.get(sourceNodeIdx).add(edge);
    }
}
