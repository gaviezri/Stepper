package stepper.flow.loader;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import stepper.flow.definition.api.FlowDefinition;
import stepper.step.StepDefinitionRegistry;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


//TODO: if errors occured throw exception to be caught and transmitted into UI instead of nulls

public class FlowLoader {
    private final DocumentBuilderFactoryImpl documentBuilderFactory;
    // constant path to flows in project_root/flow-definition-repository/flows
    private final Path PATH_TO_FLOWS = Paths.get("flow-definition-repository/flows");

    private FlowLoader() {
        documentBuilderFactory = new DocumentBuilderFactoryImpl();
        // See if necessary
        //documentBuilderFactory.setIgnoringElementContentWhitespace(true);
    }

    public Path getFlowFromRepository(String flowName) {
        // verify that the flowName is a valid .xml file and is present in the flows directory
        File flowFile = new File(PATH_TO_FLOWS.resolve(flowName).toString());
        if (!flowFile.exists() || !flowFile.isFile() || !flowFile.getName().endsWith(".xml")) {
            return null;
        }
        return flowFile.toPath();
    }
    public List<FlowDefinition> loadFlowFromXML(String fullFilePath) throws Exception {

        // verify that the flowName is a valid .xml file and is present in the given path
        File flowFile = new File(fullFilePath);
        if (!flowFile.exists() || !flowFile.isFile() || !flowFile.getName().endsWith(".xml")) {
            throw new Exception("Invalid file: " + fullFilePath + ".\nFile must be a valid .xml file.");
        }

        Document document = documentBuilderFactory.newDocumentBuilder().parse(flowFile);
        document.getDocumentElement().normalize();

        return generateListOfFlowDefinitions(document);
    }

    private List<FlowDefinition> generateListOfFlowDefinitions(Document document) throws Exception{

        if (document == null) {
            return null;
        }

        // get all flow elements from xml
        List<FlowDefinition> flowDefinitions = new ArrayList<>();
        NodeList flowDefinitionsNodeList = document.getElementsByTagName("ST-Flow");
        // check if the xml is valid (no duplicate flow names, no step names that don't exist) - V
        // and no occurrences of the following issues:
        // 1. two or more outputs share their final name - V
        // 2. there are mandatory inputs which are not user-friendly
        // 3. custom mapping issues:
        //    3.1. reference to a step/data that doesn't exist in the flow's scope
        //    3.2. reference from a later step to an earlier step
        //    3.3. attempt to map between two data's that are not of the same type
        // 4. aliasing to step/data that doesn't exist in the flow's scope - V
        // 5. flow output contains a data that doesn't exist in the flow's scope
        // 6. numerous mandatory inputs with the same name from different types!
        validateFlowDefinitionsInXML(flowDefinitionsNodeList);

        for (int i = 0; i < flowDefinitionsNodeList.getLength(); i++) {
          // create flow definitions
        }

        return flowDefinitions;
    }
    private void validateFlowDefinitionsInXML(NodeList flowDefinitionsNodeList) {

         validateFlowsNameUniqueness(flowDefinitionsNodeList);
         validateStepNameExistence(flowDefinitionsNodeList);
         validateNoTwoOutputsWithTheSameName(flowDefinitionsNodeList);
         validateStepAliasing(flowDefinitionsNodeList);
    }

    private void validateNoTwoOutputsWithTheSameName(NodeList flowDefinitionsNodeList) {

        for (int i = 0; i < flowDefinitionsNodeList.getLength(); i++) {
            Element flow = (Element) flowDefinitionsNodeList.item(i);
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

            }
        }
    }

    private void validateStepAliasing(NodeList flowDefinitionsNodeList) {

        for (int i = 0; i < flowDefinitionsNodeList.getLength(); i++) {
            Element flow = (Element) flowDefinitionsNodeList.item(i);
            NodeList stepDefinitionsNodeList = flow.getElementsByTagName("ST-StepInFlow");
            Set<String> stepNames = new HashSet<>();
            for (int j = 0; j < stepDefinitionsNodeList.getLength(); j++) {
                Element step = (Element) stepDefinitionsNodeList.item(j);
                String finalStepName = step.getAttribute("alias").isEmpty() ?
                        step.getAttribute("name") : step.getAttribute("alias");
                stepNames.add(finalStepName);
            }

            NodeList aliasDefinitionsNodeList = flow.getElementsByTagName("ST-FlowLevelAlias");
            for (int j = 0; j < aliasDefinitionsNodeList.getLength(); j++) {
                Element alias = (Element) aliasDefinitionsNodeList.item(j);
                if (!stepNames.contains(alias.getAttribute("step"))) {
                    throw new RuntimeException("Flow::<" + flow.getAttribute("name") + "> has an alias to a step that doesn't exist: " + alias.getAttribute("step"));
                }
            }
        }
    }
    private void validateFlowsNameUniqueness(NodeList flowDefinitionsNodeList) {

        Set<String> flowNames = new HashSet<>();
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
        }
    }
    private void validateStepNameExistence(NodeList flowDefinitionsNodeList) {

        for (int i = 0; i < flowDefinitionsNodeList.getLength(); i++) {

            Element flow = (Element) flowDefinitionsNodeList.item(i);
            NodeList stepDefinitionsNodeList = flow.getElementsByTagName("ST-StepInFlow");

            for (int j = 0; j < stepDefinitionsNodeList.getLength(); j++) {

                Element step = (Element) stepDefinitionsNodeList.item(j);
                String stepName = step.getAttribute("name");
                if (!StepDefinitionRegistry.getStepNames().contains(stepName)) {
                    throw new RuntimeException("Flow " + flow.getAttribute("name") + " has a step that doesn't exist: " + stepName);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        FlowLoader flowLoader = new FlowLoader();
        flowLoader.loadFlowFromXML("C:\\Users\\gavie\\OneDrive\\Desktop\\MTA\\3rd Year\\semester 2\\Java\\Stepper\\stepper-engine\\flow-definitions-repository\\flows\\ex1.xml");
        System.out.println("done");
    }

}
