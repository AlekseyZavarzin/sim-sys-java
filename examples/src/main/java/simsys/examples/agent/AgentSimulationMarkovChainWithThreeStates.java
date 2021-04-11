package simsys.examples.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import simsys.core.CoreConfig;
import simsys.core.agent.AbstractAgent;
import simsys.core.agent.Agent;
import simsys.core.annotation.Action;
import simsys.core.annotation.State;
import simsys.core.condition.TimeStopCondition;
import simsys.core.context.SimulationContext;
import simsys.core.model.AgentBasedSimulationModel;
import simsys.random.ExponentialRandomVariable;
import simsys.random.RandomVariable;

/**
 *    In this example, we consider a continuous Markov chain consisting of three states: A, B and C
 *    Initial state: A.
 *    Transition rates:
 *    q(A, B), q(A, C) = alpha (exponential)
 *    q(B, A), q(B, C) = beta (exponential)
 *    q(C, A), q(C, B) = gamma (exponential)
 */
public class AgentSimulationMarkovChainWithThreeStates {

  public static void main(String[] args) {

    Agent markovAgent = new AbstractAgent() {
      private final Random random = new Random();

      final RandomVariable alpha = new ExponentialRandomVariable(new Random(), 1);
      final RandomVariable beta = new ExponentialRandomVariable(new Random(), 2);
      final RandomVariable gamma = new ExponentialRandomVariable(new Random(), 3);

      @State(initial = true)
      private final String STATE_A = "A";
      @State
      private final String STATE_B = "B";
      @State
      private final String STATE_C = "C";

      @Action(states = {STATE_A, STATE_B, STATE_C})
      public void action() {
        double delay;
        if (currentState.equals(STATE_A)) {
          delay = alpha.nextValue();
        } else if (currentState.equals(STATE_B)) {
          delay = beta.nextValue();
        } else {
          delay = gamma.nextValue();
        }
        String new_state = defineNextState();
        System.out.println("Current State --> " + currentState + ". Next state --> " + new_state);
        moveToStateAfterTimeout(new_state, delay);
      }

      public String defineNextState() {
        ArrayList<String> states = new ArrayList<>(Arrays.asList(STATE_A, STATE_B, STATE_C));
        states.remove(currentState);
        return states.get(random.nextInt(states.size()));
      }
    };

//    SimulationContext context = SimulationContextImpl.getEmptyInstance();
    SimulationContext context = new AnnotationConfigApplicationContext(CoreConfig.class).getBean(SimulationContext.class);

    markovAgent.setContext(context);

    AgentBasedSimulationModel simulation = new AgentBasedSimulationModel(context);
    simulation.setStopCondition(new TimeStopCondition(10000));

    simulation.addAgent(markovAgent);
    simulation.run();
  }
  
}
