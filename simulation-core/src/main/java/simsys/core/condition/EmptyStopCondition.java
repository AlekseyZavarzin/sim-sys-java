package simsys.core.condition;

import simsys.core.context.SimulationContext;

import java.util.function.Predicate;

/**
 * The condition for stopping the simulation model when there are no more events in the provider.
 */
public class EmptyStopCondition implements Predicate<SimulationContext> {

  /**
   * Evaluates this predicate on the given argument.
   *
   *
   * @param context the context of the simulation model
   * @return {@code true} if the event provider is empty,
   * otherwise {@code false}
   */
  @Override
  public boolean test(SimulationContext context) {
    return context.getEventProvider().isEmpty();
  }

}
