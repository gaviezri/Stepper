package stepper.flow.definition.aliasing.manager;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAliasingManager {
    Map<String,String> stepFinalNameXDataName2AliasDataName = new HashMap<>();

    public void putAliasDataName(String stepFinalName, String dataName, String aliasDataName) {
        stepFinalNameXDataName2AliasDataName.put(stepFinalName + ":" + dataName, aliasDataName);
    }
    public String getAliasDataName(String stepFinalName, String dataName) {
        return stepFinalNameXDataName2AliasDataName.get(stepFinalName + ":" + dataName);
    }
    public String getAliasDataName(String dataName) {
        for (Map.Entry<String, String> entry : stepFinalNameXDataName2AliasDataName.entrySet()) {
            String[] step_data = entry.getKey().split(":");
            if (step_data[1].equals(dataName)) {
                    return entry.getValue();
            }
        }
        return dataName;
    }
    public String getOriginalDataName(String stepFinalName, String dataName) {
        for (Map.Entry<String, String> entry : stepFinalNameXDataName2AliasDataName.entrySet()) {
            if (entry.getValue().equals(dataName)) {
                return entry.getKey().split(":")[1];
            }
        }
        return dataName;
    }
    public boolean isAliased(String stepFinalName, String dataName) {
        return stepFinalNameXDataName2AliasDataName.containsKey(stepFinalName + ":" + dataName);
    }

    public boolean isAnAlias(String stepFinalName, String dataName) {
        for (Map.Entry<String, String> entry : stepFinalNameXDataName2AliasDataName.entrySet()) {
            if (entry.getValue().equals(dataName)) {
                return true;
            }
        }
        return false;
    }

}
