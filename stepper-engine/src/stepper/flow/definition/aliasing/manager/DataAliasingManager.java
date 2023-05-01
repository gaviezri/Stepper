package stepper.flow.definition.aliasing.manager;


import java.io.Serializable;
import java.util.*;

public class DataAliasingManager implements Serializable {
    Map<String,String> stepFinalNameXDataName2AliasDataName = new LinkedHashMap<>();

    public void putAliasDataName(String stepFinalName, String dataName, String aliasDataName) {
        stepFinalNameXDataName2AliasDataName.put(stepFinalName + ":" + dataName, aliasDataName);
    }
    public String getAliasDataName(String stepFinalName, String dataName) {
        return stepFinalNameXDataName2AliasDataName.get(stepFinalName + ":" + dataName);
    }
    public String getOriginalDataName(String stepFinalName, String dataName) {
        for (Map.Entry<String, String> entry : stepFinalNameXDataName2AliasDataName.entrySet()) {
            if (entry.getValue().equals(dataName)) {
                return entry.getKey().split(":")[1];
            }
        }
        return dataName;
    }

}
