package simsys.examples.events.timer;


import java.util.Random;
import simsys.core.condition.TimeStopCondition;
import simsys.core.context.SimulationContext;
import simsys.core.context.SimulationContextImpl;
import simsys.core.event.HandledEvent;
import simsys.core.event.HandledEventBuilderFactory;
import simsys.core.model.SimulationModelImpl;
import simsys.random.ExponentialRandomVariable;

/**
 * Implementation of a timer with two events. The first event (constPeriodic) is activated at
 * regular intervals. The second event (randomPeriodic) is activated at intervals of exponential
 * distribution with some rate.
 */
public class TimerEventBuilderFactorySimulation {

  public static void main(String[] args) {
    SimulationContext context = SimulationContextImpl.getContext();

    HandledEventBuilderFactory eventBuilderFactory = new HandledEventBuilderFactory(
        context,
        event -> System.out.println("Before handler for event " + event),
        event -> System.out.println("After handler for event " + event));

    HandledEvent randomPeriodic = eventBuilderFactory
        .create()
        .periodic(new ExponentialRandomVariable(new Random(), 1))
        .addHandler(event -> System.out
            .println("Message from periodic random event: " + event.getActivateTime()))
        .build();

    HandledEvent constPeriodic = eventBuilderFactory
        .create()
        .periodic(3)
        .addHandler(event -> System.out
            .println("Message from periodic const event: " + event.getActivateTime()))
        .build();

    context.getEventProvider().add(randomPeriodic);
    context.getEventProvider().add(constPeriodic);

    SimulationModelImpl model = new SimulationModelImpl(context);
    model.setStopCondition(new TimeStopCondition(1_000));
    model.run();
  }

}
