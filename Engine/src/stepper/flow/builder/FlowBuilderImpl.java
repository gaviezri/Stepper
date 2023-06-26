package stepper.flow.builder;

import javafx.util.Pair;
import stepper.dd.api.DataDefinition;
import stepper.flow.definition.api.FlowDefinition;
import stepper.flow.definition.impl.FlowDefinitionImpl;
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.step.api.StepDefinition;
import stepper.step.impl.ZipperStep;

import java.io.Serializable;
import java.util.*;

public class FlowBuilderImpl implements FlowBuilder, Serializable {
    private List<FlowDefinition> flowDefinitions = new ArrayList<>();
    private Map<String, Class<?extends Enum>> enumInputName2InputVal = new HashMap<>();


    public FlowBuilderImpl(){
        /** EVERY STEP WITH ENUM-INPUT NEEDS TO BE MAPPED HERE FOR FUTURE VALIDATION!*/

        enumInputName2InputVal.put("OPERATION", ZipperStep.OperationType.class);

//          curEnum.clear();
//         TODO: add enum formal types
//        curEnum.add("GET");
//        curEnum.add("PUT");
//        curEnum.add("POST");
//        curEnum.add("DELETE");
//        enumInputName2InputVal.put("METHOD", new ArrayList<>(curEnum));
    }

    public FlowDefinition getFlowByName(String name){
        FlowDefinition res = null;
        if(this.doesThisFlowExist(name)){
            for (int i=0;i<flowDefinitions.size();++i){
                if(name.equals(flowDefinitions.get(i).getName())){
                    res = flowDefinitions.get(i);
                }
            }
        }
        return res;
    }
    public List<FlowDefinition> buildFlows() {
        // CHECK THAT:
        // reference from a later step to an earlier step - V
        // attempt to map between two data's that are not of the same type - V
        // there are mandatory inputs which are not user-friendly - V
        // flow output contains a data that doesn't exist in the flow's scope - V
        // numerous mandatory inputs with the same name from different types! - V
        // AND THEN OR SIMULTANEOUSLY
        // create list of step usage declaration for each flow in list
        for (FlowDefinition flowDefinition : flowDefinitions) {
            flowDefinition.createMapping();
            flowDefinition.validateFlowStructure();
            flowDefinition.setAccessibility();
            flowDefinition.setFreeInputs();
//            flowDefinition.setInitialInputs();
        }
        return flowDefinitions;
    }
    @Override
    public boolean doesThisFlowExist(String flowName){
        return flowDefinitions.stream().map(FlowDefinition::getName).anyMatch(fn -> fn.equals(flowName));
    }

    @Override
    public boolean isInputOfFlow(String flowName, String dataName){
        /** checks if this ORIGINAL name an input of the flow in index 'flowInd'*/
        return this.getFlowByName(flowName).isInputOfFlow(dataName);
    }

    @Override
    public boolean isOutputOfFlow(String flowName, String dataName){
        /** checks if this ORIGINAL name an output of the flow in index 'flowInd'*/
        return this.getFlowByName(flowName).isOutputOfFlow((dataName));
    }

    @Override
    public void addTargetFlowToFlowsContinuation(int srcFlowInd, String targetFlowName){
        flowDefinitions.get(srcFlowInd).getContinuation().addTargetFlow(targetFlowName);
    }

    @Override
    public void addSrc2DataToFlowContinuationByTargetFlowsName(int srcFlowInd, String targetFlowName, String srcData, String targetData){
        flowDefinitions.get(srcFlowInd).getContinuation().addDataMappingPairToCurTargetFlow(new Pair<>(srcData,targetData));
    }

    @Override
    public List<String> getAllFlowsInputsFinalNamesByIndex(int flowInd){
        return flowDefinitions.get(flowInd).getAllInputsFinalNames();
    }

    public void reset() {
        flowDefinitions = new LinkedList<>();
    }

    @Override
    public void addFlowsInitialInputValues(String inputName, Object initialValue, int flowInd, DataDefinition curDD){
        flowDefinitions.get(flowInd).addFlowsInitialInputValues(inputName,initialValue, curDD);
    }

    @Override
    public void instantiateFlowsAndSetNames(List<String> flowNames) {
        for (String flowName : flowNames) {
            flowDefinitions.add(new FlowDefinitionImpl(flowName));
        }
    }
    @Override
    public void addStepToFlow(int flowidx, String name, String finalname, boolean skipIfFail) {
        flowDefinitions.get(flowidx).addFinalizedStep(name, finalname, skipIfFail);
    }

    @Override
    public void setFlowFormalOutputs(int flowidx, Set<String> flowFormalOutputs) {
        List<String> formaloutputs = new ArrayList<>(flowFormalOutputs);
        flowDefinitions.get(flowidx).setFlowFormalOutputs(formaloutputs);
    }


    @Override
    public void setFlowDescription(List<String> description){
        for (int flowidx = 0 ; flowidx < description.size(); flowidx++) {
            flowDefinitions.get(flowidx).setDescription(description.get(flowidx));
        }

    }

    @Override
    public void addFlowLevelAlias(int flowidx, String stepFinalName, String sourceDataName, String sourceDataAlias) {
        flowDefinitions.get(flowidx).addFlowLevelAlias(stepFinalName, sourceDataName, sourceDataAlias);
    }

    @Override
    public String getStepOriginalName(int flowidx, String stepName){
        return flowDefinitions.get(flowidx).getStepOriginalName(stepName);
    }

    @Override
    public void addCustomMapping(int flowidx, String stepName, String dataName, String targetStepName, String targetDataName) {
        flowDefinitions.get(flowidx).addCustomMapping(stepName, dataName, targetStepName, targetDataName);
    }
    @Override
    public String getResourceFinalName(int flowidx, String StepFinalName, String DataName) {
        return flowDefinitions.get(flowidx).getResourceFinalName(StepFinalName, DataName);
    }


    @Override
    public String getStepFinalName(int flowidx, String sourceStepName) {
            return flowDefinitions.get(flowidx).doesThisFinalStepNameExists(sourceStepName);
    }

    @Override
    public DataDefinition getCorespondingDataDef(String inputName, int flowInd) {
        /** returns the corresponding DataDefinition according to input name*/
        String orgInputName = flowDefinitions.get(flowInd).getDataDefOriginalName(inputName);

        if(orgInputName == null){ //    no such input name in any of the current flow steps
           throw new IllegalArgumentException("No step in the current flow has an input with final name: \"" + inputName + "\" ");
        }

        DataDefinition resDD = null;

        List<StepUsageDeclaration> curFlowSteps = flowDefinitions.get(flowInd).getFlowSteps();
        StepDefinition curStp;

        for (StepUsageDeclaration stp : curFlowSteps) {
            resDD = stp.getDataDefByName(orgInputName);
            if (resDD != null){break;}
        }

        return resDD;
    }
    @Override
    public Enum isValidEnumInputNameAndValue(String name, String value){
        return Enum.valueOf(enumInputName2InputVal.get(name), value);
    }

//    public Enum isValidEnumInputNameAndValue(String name, String value){
//        List<Enum> curEnumList;
//        for (String inputName:enumInputName2InputVal.keySet()) {
//            if(inputName.equals(name)){
//                curEnumList = enumInputName2InputVal.get(inputName);
//                for (Enum e:curEnumList){
//                    if(e.toString().equals(value)){
//                        return e;
//                    }
//                }
//            }
//        }
//        return null;
//    }

    @Override
    public String getFlowNameByInd(int flowInd){
        return flowDefinitions.get(flowInd).getName();
    }

    @Override
    public FlowDefinition getFlowByInd(int flowidx) {
        return flowDefinitions.get(flowidx);
    }
}
