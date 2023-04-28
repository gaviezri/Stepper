package stepper.controller;

import stepper.dto.DTO;
import stepper.flow.execution.runner.FlowExecutor;
import stepper.flow.loader.FlowLoader;

public class EngineController {
    FlowExecutor flowExecutor = new FlowExecutor();
    FlowLoader flowLoader = new FlowLoader();

    public DTO readXML(String Path){return null;}
    //get all dtos
    //execute flow

}
