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
import stepper.flow.definition.api.StepUsageDeclaration;
import stepper.step.StepDefinitionRegistry;
import stepper.step.api.DataDefinitionDeclaration;
import stepper.step.api.StepDefinition;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;


//TODO: if errors occured throw exception to be caught and transmitted into UI instead of nulls

public class FlowLoader implements Serializable {
    public void reset() {
        builder.reset();
    }

    public class StepNameExistenceException extends Exception {
        public StepNameExistenceException(String flowName, String StepName) {
            super(StepName + " step does not exist in flow: " + flowName);
        }
    }
    public class ResourceNameExistenceException extends Exception {
        public ResourceNameExistenceException(String StepName, String ResourceName)
        {
            super(ResourceName + " does not exist in step: " + StepName);
        }
    }
    public class MappingOrderException extends Exception {
        public MappingOrderException(String flowName, String Source, String Target){
            super("Step " + Source + " is mapped to step " + Target + " but " + Target + " is executed before " + Source + " in flow: " + flowName);
        }
    }

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

    public List<FlowDefinition> loadFlowFromXML(InputStream XMLFileStream) throws Exception {
        // servlet version
        Document document = new DocumentBuilderFactoryImpl().newDocumentBuilder().parse(XMLFileStream);
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
            throw new RuntimeException("Element \"ST-ThreadPool\" is not in the given XML file!");
        }

        // builder will be used to create the flow definitions
        // and will work along the validations to minimize the number of iterations over the xml

        // get all flow elements from xml
        try {
            NodeList flowDefinitionsNodeList = document.getElementsByTagName("ST-Flow");
            validateFlowDefinitionsInXML(flowDefinitionsNodeList);
        } catch (Exception e) {
            builder.reset();
            throw new Exception(e.getMessage());
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
         validateContinuation(flowDefinitionsNodeList);
    }

    private void validateContinuation(NodeList flowDefinitionsNodeList) {
        String targetFlow = null;
        String sourceData = null;
        String targetData = null;
        Element continuation = null;
        Element curContinuationMapping = null;

        for (int srcFlowInd = 0; srcFlowInd < flowDefinitionsNodeList.getLength(); srcFlowInd++) {
            Element flow = (Element) flowDefinitionsNodeList.item(srcFlowInd);

            if (flow.getElementsByTagName("ST-Continuations").getLength() != 0) {   // this is an optional attribute
                NodeList flowContinuations = flow.getElementsByTagName("ST-Continuation");
                for (int curContinuationInd = 0; curContinuationInd < flowContinuations.getLength(); curContinuationInd++) {
                    continuation = (Element) flowContinuations.item(curContinuationInd);
                    targetFlow = continuation.getAttribute("target-flow");
                    this.validateTargetFlowNameAndAddToBuilder(srcFlowInd, targetFlow);

                    NodeList curTargetFlowsContinuationsMapping = continuation.getElementsByTagName("ST-Continuation-Mapping");
                    for (int t = 0; t < curTargetFlowsContinuationsMapping.getLength(); t++) {
                        curContinuationMapping = (Element) curTargetFlowsContinuationsMapping.item(t);
                        sourceData = curContinuationMapping.getAttribute("source-data");
                        targetData = curContinuationMapping.getAttribute("target-data");
                        validateDataAndAddToBuilder(srcFlowInd, sourceData, targetData, targetFlow, builder.getFlowNameByInd(srcFlowInd));
                    }
                }
            }
        }
    }

    private void validateDataAndAddToBuilder(int srcFlowInd, String srcDataName, String targetDataName, String targetFlowName, String srcFlowName){
        /** we check for Inputs in the Target Flow and for Outputs in the Source flow*/

        if(!builder.isInputOfFlow(targetFlowName,targetDataName)){
             throw(new IllegalArgumentException("Target Flow \"" + targetFlowName + "\" has no input with name " + targetDataName + "."));
        }
        else if(!builder.isOutputOfFlow(srcFlowName,srcDataName)){
            throw(new IllegalArgumentException("Source Flow \"" + srcFlowName + "\" has no output with name " + srcDataName + "."));
        }
        else{
            builder.addSrc2DataToFlowContinuationByTargetFlowsName(srcFlowInd,targetFlowName,srcDataName,targetDataName);
        }
    }

    private void validateTargetFlowNameAndAddToBuilder(int srcFlowInd, String flowName){
        if(this.builder.doesThisFlowExist(flowName)){
            this.builder.addTargetFlowToFlowsContinuation(srcFlowInd, flowName);
        }
        else{
            throw(new IllegalArgumentException("No flow with name " + flowName + " does not exist!"));
        }
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
                    findCustomMappingResource(flowidx, stepName, dataName, targetStepName, targetDataName);
                } catch (StepNameExistenceException e) {
                    throw new RuntimeException(e.getMessage());
                } catch (ResourceNameExistenceException e) {
                    throw new RuntimeException(e.getMessage());
                } catch (MappingOrderException e ) {
                    throw new RuntimeException(e.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException("Custom Mapping is invalid - please check it.\n" + e.getMessage());
                }
                builder.addCustomMapping(flowidx, stepName, dataName, targetStepName, targetDataName);
            }
        }
    }

    private void findCustomMappingResource(int flowidx, String sourceStepName, String sourceDataName, String targetStepName, String targetDataName) throws Exception {
        StepUsageDeclaration sourceStepUsage = builder.getFlowByInd(flowidx).getStepUsageDeclarationByFinalName(sourceStepName);
        StepUsageDeclaration targetStepUsage = builder.getFlowByInd(flowidx).getStepUsageDeclarationByFinalName(targetStepName);
        // validate source step exists in flow
        if (sourceStepUsage == null) {
            throw new StepNameExistenceException(builder.getFlowByInd(flowidx).getName(),sourceStepName);
        }
        // validate target step exists in flow
        if (targetStepUsage == null) {
            throw new StepNameExistenceException(builder.getFlowByInd(flowidx).getName(),targetStepName);
        }
        // validate source data exists in step
        if  (sourceStepUsage.getResourceFinalName(sourceDataName) == null){
            throw new ResourceNameExistenceException(sourceStepName, sourceDataName);
        }
        // validate target data exists in step
        if  (targetStepUsage.getResourceFinalName(targetDataName) == null){
            throw new ResourceNameExistenceException(targetStepName, targetDataName);
        }
        // validate source step comes before target step
        if (sourceStepUsage.getStepOrder() > targetStepUsage.getStepOrder()) {
            throw new MappingOrderException(builder.getFlowByInd(flowidx).getName(),sourceStepName, targetStepName);
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
                    initialValue = initialInputValue.getAttribute("initial-value").toUpperCase();
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
