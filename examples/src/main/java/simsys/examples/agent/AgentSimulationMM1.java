package simsys.examples.agent;

import java.util.Random;
import simsys.component.agents.SourceAgent;
import simsys.component.agents.SystemAgent;
import simsys.core.condition.TimeStopCondition;
import simsys.core.context.SimulationContextImpl;
import simsys.core.model.AgentBasedSimulationModel;
import simsys.random.ExponentialRandomVariable;

public class AgentSimulationMM1 {

  public static void main(String[] args) {

    double lambda = 1;
    SourceAgent source = new SourceAgent(new ExponentialRandomVariable(new Random(), lambda));

    double mu = 2;
    SystemAgent system = new SystemAgent(new ExponentialRandomVariable(new Random(), mu));

    source.setReceiver(system);


    AgentBasedSimulationModel agentSimulationMM1 = new AgentBasedSimulationModel(
        SimulationContextImpl.getEmptyInstance());

    agentSimulationMM1.setStopCondition(new TimeStopCondition(50));

    agentSimulationMM1.addAgent(source);
    agentSimulationMM1.addAgent(system);

    agentSimulationMM1.run();

  }
}
