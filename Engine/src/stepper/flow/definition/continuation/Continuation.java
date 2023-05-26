package stepper.flow.definition.continuation;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Continuation {
    Map<String, List<Pair<String,String>>> targetFlowName2DataMappings = new HashMap<>();

    /** allows adding flows to Continuations object in 2 steps:
     * 1. Target flow name
     * 2. List of pairs representing (Source Data Org Name, Target Data Org Name)
     * by that keeping the user unaware of the implementations details */
    String curTargetFlow;
    public void addTargetFlow(String targetFlowName){
        curTargetFlow = targetFlowName;
        if(!targetFlowName2DataMappings.containsKey(curTargetFlow)){
            targetFlowName2DataMappings.put(targetFlowName,new ArrayList<Pair<String,String>>());
        }
    }

    public void addDataMappingPairToCurTargetFlow(Pair<String,String> src2TargetData){
        targetFlowName2DataMappings.get(curTargetFlow).add(src2TargetData);
    }

    public boolean canContinueWith(String flowName){
        /** Checks if current flow's data can be used to initialize the given flow name values
         * (==can the flow given continue the current flow)*/
        return targetFlowName2DataMappings.containsKey(flowName);
    }

    public List<Pair<String,String>> getContinuationsByName(String flowName){
        return targetFlowName2DataMappings.get(flowName);
    }

    public Integer getContinuationsCount(){
        return targetFlowName2DataMappings.size();
    }
}
