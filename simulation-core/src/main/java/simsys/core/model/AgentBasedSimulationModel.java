package simsys.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import simsys.core.agent.Agent;
import simsys.core.context.SimulationContext;

public class AgentBasedSimulationModel extends AbstractSimulationModel {

  protected List<Agent> agents;

  public AgentBasedSimulationModel(SimulationContext simulationContext) {
    this.simulationContext = simulationContext;
    this.agents = new ArrayList<>();
  }

  public void addAgent(Agent agent) {
    this.agents.add(agent);
  }

  public void addAgents(List<Agent> agents) {
    this.agents.addAll(agents);
    //TODO: проверить на всех уровнях, что агенты имееют разные имена
  }

  public List<Agent> getAgentsByClass(Class<?> clazz) {
    List<Agent> agentsList = new ArrayList<>();
    for (Agent agent : this.agents) {
      if (agent.getClass() == clazz) {
        agentsList.add(agent);
      }
    }
    if (agentsList.isEmpty()) {
      throw new NoSuchElementException("No agents found with this class " + clazz.getName());
    }
    return agentsList;
  }

  public List<Agent> getAgents() {
    return this.agents;
  }

}
