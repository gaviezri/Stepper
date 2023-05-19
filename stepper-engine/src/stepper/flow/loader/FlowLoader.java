package stepper.flow.loader;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import stepper.dd.api.DataDefinition;
import stepper.flow.builder.FlowBuilder;
import stepper.flow.builder.FlowBuilderImpl;
import stepper.flow.definition.api.FlowDefinition;
import stepper.step.StepDefinitionRegistry;
import stepper.step.api.DataDefinitionDeclaration;
import stepper.step.api.StepDefinition;

import java.io.File;
import java.io.Serializable;
import java.util.*;


//TODO: if errors occured throw exception to be caught and transmitted into UI instead of nulls

public class FlowLoader implements Serializable {

    private FlowBuilder builder = new FlowBuilderImpl();
    private Integer threadCount = 1;
    public List<FlowDefinition> loadFlowFromXML(String flowFileName) throws Exception {

        // verify that the flowName is a valid .xml file and is present in the given path
        File flowFile = new File(flowFileName);
        if (!flowFile.exists() || !flowFile.isFile() || !flowFile.getName().endsWith(".xml")) {
            throw new Exception("Invalid file: " + flowFileName + ".\nFile must be a valid .xml file.");
        }
        Document document = new DocumentBuilderFactoryImpl().newDocumentBuilder().parse(flowFile);
        document.getDocumentElement().normalize();
        builder.reset();
        initialFlowValidations(document);
        return builder.buildFlows();
    }
    private void initialFlowValidations(Document document) throws Exception{
        String threadCount;
        try {
            threadCount = document.getElementsByTagName("ST-ThreadPool").item(0).getTextContent();
            this.threadCount = validateThreadCount(threadCount);
        } catch (NullPointerException e) {
            throw new RuntimeException("Element \"ST-ThreadPool\" is not in the given XML file! Stepper will work synchronized");
        }

        // builder will be used to create the flow definitions
        // and will work along the validations to minimize the number of iterations over the xml
        finally{
            // get all flow elements from xml
            NodeList flowDefinitionsNodeList = document.getElementsByTagName("ST-Flow");
            validateFlowDefinitionsInXML(flowDefinitionsNodeList);
        }
    }
    private void validateFlowDefinitionsInXML(NodeList flowDefinitionsNodeList) throws Exception {

         validateFlowsNameUniqueness(flowDefinitionsNodeList); // builder gets flow names and descriptions here
        // and instantiate number of flow-definitions - V
         validateStepNameExistence(flowDefinitionsNodeList); // builder adds to each flow the steps it contains - V
         validateNoTwoOutputsWithTheSameNameForEachFlow(flowDefinitionsNodeList); // builder sets each flow's formal outputs - V
         validateStepAliasing(flowDefinitionsNodeList); // builder gets the step aliasing and adds it to the flow - V
         validateDataAliasing(flowDefinitionsNodeList); // builder gets the flow aliasing and adds it to the flow - V
         validateCustomMapping(flowDefinitionsNodeList); // builder gets the custom mapping and adds it to the flow - V
         validateInitialInputValues(flowDefinitionsNodeList);
    }

    private Integer validateThreadCount(String threadCountStr) {
        Integer threadCountInt;
        try {
            threadCountInt = Integer.parseInt(threadCountStr);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("The value given in element \"ST-ThreadPool\" is not numeric. ");
        }
        if(threadCountInt<=0){
            throw new IllegalArgumentException("Number of threads must be larger then 1!");
        }
        return threadCountInt;
    }


    //TODO: implement
    private void validateDataAliasing(NodeList flowDefinitionsNodeList) throws Exception {
        for (int flowidx = 0; flowidx < flowDefinitionsNodeList.getLength(); flowidx++) {
            // iterate flows
            Element flow = (Element) flowDefinitionsNodeList.item(flowidx);
            NodeList flowLevelAliasings = flow.getElementsByTagName("ST-FlowLevelAlias");
            // iterate data alias declarations
            for (int aliasing = 0; aliasing < flowLevelAliasings.getLength(); aliasing++) {
                String stepName = ((Element) flowLevelAliasings.item(aliasing)).getAttribute("step");
                String dataName = ((Element) flowLevelAliasings.item(aliasing)).getAttribute("source-data-name");
                String dataAlias = ((Element) flowLevelAliasings.item(aliasing)).getAttribute("alias");
                // validate all fields are set
                if (stepName.isEmpty() || dataName.isEmpty() || dataAlias.isEmpty()) {
                    throw new RuntimeException("Invalid flow-level aliasing declaration in flow " + flow.getAttribute("name") + ".\n" +
                            "All fields must be set. element No." + aliasing);
                }

                boolean found = findAliasingResouce(flowidx, stepName, dataName, dataAlias);
                if (!found) {
                    throw new RuntimeException("Data " + dataName + " doesn't exist in step " + stepName + " in flow " + flow.getAttribute("name"));
                }
                // set the aliasing in the relevant flow relevant step dictionary
            }

        }
    }

    private boolean findAliasingResouce(int flowidx, String stepName, String dataName, String dataAlias) {
        // check if step exists and if data exists in step's inputs/outputs
        String stepOriginalName = builder.getStepOriginalName(flowidx, stepName);
        StepDefinition step = StepDefinitionRegistry.convertFromUserFriendlyToInternal(stepOriginalName).getStepDefinition();
        List<DataDefinitionDeclaration> inputs = step.inputs();
        List<DataDefinitionDeclaration> outputs = step.outputs();
        boolean found = false;
        for (DataDefinitionDeclaration input : inputs) {
            if (input.getName().equals(dataName)) {
                String finalName = dataAlias.isEmpty() ? dataName : dataAlias;
                builder.addFlowLevelAlias(flowidx, stepName, dataName, finalName);
                found = true;
                break;
            }
        }
        if (!found){
            for (DataDefinitionDeclaration output : outputs) {
                if (output.getName().equals(dataName)) {
                    String finalName = dataAlias.isEmpty() ? dataName : dataAlias;
                    builder.addFlowLevelAlias(flowidx, stepName, dataName, finalName);
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    //TODO: implement
    private void validateCustomMapping(NodeList flowDefinitionsNodeList) {
        for (int flowidx = 0; flowidx < flowDefinitionsNodeList.getLength(); flowidx++) {
            // iterate flows
            Element flow = (Element) flowDefinitionsNodeList.item(flowidx);
            NodeList customMappings = flow.getElementsByTagName("ST-CustomMapping");
            // iterate custom mapping declarations
            for (int mapping = 0; mapping < customMappings.getLength(); mapping++) {
                String stepName = ((Element) customMappings.item(mapping)).getAttribute("source-step");
                String dataName = ((Element) customMappings.item(mapping)).getAttribute("source-data");
                String targetStepName = ((Element) customMappings.item(mapping)).getAttribute("target-step");
                String targetDataName = ((Element) customMappings.item(mapping)).getAttribute("target-data");
                // validate all fields are set
                if (stepName.isEmpty() || dataName.isEmpty() || targetStepName.isEmpty() || targetDataName.isEmpty()) {
                    throw new RuntimeException("Invalid custom mapping declaration in flow " + flow.getAttribute("name") + ".\n" +
                            "All fields must be set. element No." + mapping);
                }
                try {
                    Pair<Boolean, String> found = findCustomMappingResouce(flowidx, stepName, dataName, targetStepName, targetDataName);
                    if (!found.getKey()) {
                        throw new RuntimeException("Data " + found.getValue() + " doesn't exist in step " + stepName + " in flow " + flow.getAttribute("name"));
                    }
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Step " + stepName + " doesn't exist in flow " + flow.getAttribute("name"));
                }
                builder.addCustomMapping(flowidx, stepName, dataName, targetStepName, targetDataName);
            }
        }
    }

    private Pair<Boolean, String> findCustomMappingResouce(int flowidx, String sourceStepName, String sourceDataName, String targetStepName, String targetDataName) {
        // check if step exists and if data exists in step's inputs/outputs
        String sourceStepFinalName = builder.getStepFinalName(flowidx, sourceStepName);
        String sourceDataFinalName = builder.getResourceFinalName(flowidx, sourceStepFinalName, sourceDataName);

        String targetStepFinalName = builder.getStepFinalName(flowidx, targetStepName);
        String targetDataFinalName = builder.getResourceFinalName(flowidx, targetStepFinalName, targetDataName);

        if  (!sourceStepName.equals(sourceStepFinalName)){
            return new Pair<>(false, sourceStepName);

        } else if (! sourceDataName.equals(sourceDataFinalName)) {
            return new Pair<>(false, sourceDataName);

        } else if (! targetStepName.equals(targetStepFinalName)) {
            return new Pair<>(false, targetStepName);

        } else if (! targetDataName.equals(targetDataFinalName)) {
            return new Pair<>(false, targetDataName);

        } else {
            return new Pair<>(true, null);
        }
    }

    private void validateNoTwoOutputsWithTheSameNameForEachFlow(NodeList flowDefinitionsNodeList) {

        for (int flowidx = 0; flowidx < flowDefinitionsNodeList.getLength(); flowidx++) {
            Element flow = (Element) flowDefinitionsNodeList.item(flowidx);
            NodeList outputDefinitionsNodeList = flow.getElementsByTagName("ST-FlowOutput");
            Set<String> outputNames = new HashSet<>();
            for (int j = 0; j < outputDefinitionsNodeList.getLength(); j++) {
                Element output = (Element) outputDefinitionsNodeList.item(j);
                String[] outputNamesList = output.getTextContent().split(",");
                for (String outputName : outputNamesList) {
                    if (outputNames.contains(outputName)) {
                        throw new RuntimeException("Flow " + flow.getAttribute("name") + " has two outputs with the same name: " + outputName);
                    }
                    outputNames.add(outputName);
                }
                builder.setFlowFormalOutputs(flowidx, outputNames);

            }
        }
    }
    private void validateStepAliasing(NodeList flowDefinitionsNodeList) {

        for (int flowidx = 0; flowidx < flowDefinitionsNodeList.getLength(); flowidx++) {
            // iterate flows
            Element flow = (Element) flowDefinitionsNodeList.item(flowidx);
            NodeList stepDefinitionsNodeList = flow.getElementsByTagName("ST-StepInFlow");
            // iterate steps
            for (int stepidx = 0; stepidx < stepDefinitionsNodeList.getLength(); stepidx++) {

                String stepName = ((Element) stepDefinitionsNodeList.item(stepidx)).getAttribute("name");
                String stepAlias = ((Element) stepDefinitionsNodeList.item(stepidx)).getAttribute("alias");
                String finalName = stepAlias.isEmpty() ? stepName : stepAlias;
                String skipIfFail = ((Element) stepDefinitionsNodeList.item(stepidx)).getAttribute("continue-if-failing");
                builder.addStepToFlow(flowidx, stepName, finalName, skipIfFail.equals("true"));
            }
        }
    }

    private void validateInputNameAndValueAndAddToFlowDef(String inputName, String inputValue, int flowInd, String flowName){
        DataDefinition curDD = builder.getCorespondingDataDef(inputName, flowInd);
        Object inputVal;
        if(curDD == null){  // there is no input with the name "input name" in the current flow.
            throw new IllegalArgumentException("Flow \"" + flowName + "\" has a no step that gets: \"" + inputName + "\" as input.");
        }

        if(curDD.getType() == Enum.class){ // there is no enum with the value "input value" in the current flow.
            inputVal = builder.isValidEnumInputNameAndValue(inputName, inputValue);
            if(inputVal==null){
                throw new IllegalArgumentException(inputValue + " is not part of the enum that input named " + inputName + " is expecting");
            }
        }
        else{
            try{
                inputVal = curDD.getType().cast(inputValue);
            } catch (ClassCastException  e) {
                throw new ClassCastException ("The input \"" + inputName + "\" cannot be created from " + inputValue + ".");
            }
        }
        builder.addFlowsInitialInputValues(inputName,inputVal,flowInd, curDD);
    }

    public Integer getWorkersCount(){
        return this.threadCount;
    }
    private void validateInitialInputValues(NodeList flowDefinitionsNodeList) {
        String inputName;
        String initialValue;
        Element initialInputValue;

        for (int i = 0; i < flowDefinitionsNodeList.getLength(); i++) {
            Element flow = (Element) flowDefinitionsNodeList.item(i);

            if (flow.getElementsByTagName("ST-InitialInputValues").getLength() != 0) {   // this is an optional attribute
                NodeList flowInitialInputValues = flow.getElementsByTagName("ST-InitialInputValue");

                for (int j = 0; j < flowInitialInputValues.getLength(); j++) {
                    initialInputValue = (Element) flowInitialInputValues.item(j);
                    inputName = initialInputValue.getAttribute("input-name");
                    initialValue = initialInputValue.getAttribute("initial-value");
//                  this function validates the initial input received from the XML file and assign them to the corresponding data definition
                    validateInputNameAndValueAndAddToFlowDef(inputName, initialValue,i,flow.getAttribute("name"));

                }
            }
        }
    }

    private void validateFlowsNameUniqueness(NodeList flowDefinitionsNodeList) {

        List<String> flowNames = new ArrayList<>();
        List<String> flowDescriptions = new ArrayList<>();
        for (int i = 0; i < flowDefinitionsNodeList.getLength(); i++) {
            Element flow = (Element) flowDefinitionsNodeList.item(i);
            if ( flow.getAttribute("name").isEmpty()) {
                throw new RuntimeException("Flow name is empty");
            }
            String flowName = flow.getAttribute("name");
            if (flowNames.contains(flowName)) {
                throw new RuntimeException("Flow name is not unique: " + flowName);
            }
            flowNames.add(flow.getAttribute("name"));
            flowDescriptions.add(flow.getElementsByTagName("ST-FlowDescription").item(0).getTextContent());
        }
        builder.instantiateFlowsAndSetNames(flowNames);
        builder.setFlowDescription(flowDescriptions);

    }
    private void validateStepNameExistence(NodeList flowDefinitionsNodeList) {

        for (int flowidx = 0; flowidx < flowDefinitionsNodeList.getLength(); flowidx++) {

            Element flow = (Element) flowDefinitionsNodeList.item(flowidx);
            NodeList stepDefinitionsNodeList = flow.getElementsByTagName("ST-StepInFlow");

            for (int j = 0; j < stepDefinitionsNodeList.getLength(); j++) {

                Element step = (Element) stepDefinitionsNodeList.item(j);
                String stepName = step.getAttribute("name");
                if (!StepDefinitionRegistry.getStepNames().contains(stepName)) {
                    throw new RuntimeException("Flow \"" + flow.getAttribute("name") + "\" has a step that doesn't exist: \"" + stepName + "\"" );
                }
            }
        }
    }
}
