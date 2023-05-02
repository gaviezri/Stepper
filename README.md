 STEPPER Ex1.  | Play Me While You Read --> https://www.youtube.com/watch?v=w0N4twV28Mw&ab_channel=youpi444 |

| Omri Shahar| 315873471 | omrisr@mta.ac.il 
| Gal Aviezri    | 208782912 galae@gmta.ac.il 

We have decided to implement 3 different modules: the engine, the UI and the main controller as separate modules
and have it orchestrate the interaction between the engine modules and the UI.
this allows an easier modularity and refactorization.

the stepper application allows the user to execute a set of instructions pre-defined on an .xml file.
this somewhat allows a mean of automation and re-execution of the same actions again and again.
the stepper also collects statistics and can show extensive details of selected "flows”.

we have used the supplied infrastructure and built the project from that starting point.
it felt like every small data structure had to be aggregated with some other data
and logic, so, we created "managers" to handle the data of another logic piece.
For instance, each step has a manager in the execution context which manages the step execution data (aggregation of step result, output, duration etc.)
and also manages the step's logs.

EngineController allows a convenient api to the monolith which is the engine.
this modularity grants the ease of replacement between implementations of the engine module.
the main engine component is called EngineController 
which aggregates [1. Execution Archive, 2. FlowLoader, 3. FlowLibrary, 4.FlowExecuter, 5.StatisticsManager]

1. Execution Archive
--------------------
aggregates a stack of FlowExecution(s)
used to archive past executions and retrieval of their information for statistics and investigative purposes.

2. Flow Loader
-------------
aggregates a FlowBuilder[2.1]
the Loader is the component which, given a .xml file which presumably contains a valid flow definition,
parses the xml, extracts the relevant data from it and delegates it to the Builder.

2.1. Flow Builder
----------------
the builder is responsible to build the flows in the broader sense.
after the loader delegates the dry data from the xml, the builder will create a list of Flow Definitions 
which in their turn will form the complex relations that defines them (mapping graph that maps a step's output to another step's input, aliasing mechanism for steps and resource etc.)

3. Flow Library
---------------
aggregates a list of all recently loaded flow definitions.
act as a storage for the definitions that can be retrieved on demand.

4. Flow Executer
----------------
a stateless class in charge of executing a flow. 
through ::ExecuteFlow which receives a FlowExecution, an aggregation that contains all the necessary data (managers of steps, loggers of steps, context object etc.)
and executes the Flow step by step.

5. Statistics Manager
---------------------
this manager expertise is generating statistics of flows and steps based the information available from the Archive.

Capturing and Loading System's State
------------------------------------
The architecture of encapsulating all the system's state in the EngineController object, allows us to only serialize that object.
With the Java native serialization framework, the user is request to provide a path to a directory (not a file!) in which he would like to the dump the serialized object.
Then when loading the system's state from a saved snapshot, the user, again, will only have to provide the path the directory (not the file) in which he previously saved the Stepper.ser file.
of course we had to go through the entire tree of objects which are being serialized and make them implement Serializable...




