package simsys.core.model;

import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simsys.core.context.SimulationContext;
import simsys.core.event.Event;

@Slf4j
public abstract class AbstractSimulationModel implements SimulationModel {

  protected SimulationContext simulationContext;
  protected Predicate<SimulationContext> stopCondition;

  @Override
  public void run() {
    while (!this.stopCondition.test(this.simulationContext)) {
      step();
    }
  }

  @Override
  public void step() {
    Event nextEvent = this.simulationContext.getEventProvider().getNext();
    this.simulationContext.getClock().setCurrentTime(nextEvent.getActivateTime());
    LOGGER.debug("A new EVENT!  The current time: {}", this.simulationContext.getCurrentTime());
    nextEvent.activate();
    this.simulationContext.updateDeltaTimeLastTwoEvents();
  }

  public Predicate<SimulationContext> getStopCondition() {
    return this.stopCondition;
  }

  public void setStopCondition(Predicate<SimulationContext> stopCondition) {
    this.stopCondition = stopCondition;
  }

}
