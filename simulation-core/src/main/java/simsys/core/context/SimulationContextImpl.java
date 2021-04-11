package simsys.core.context;

import org.springframework.beans.factory.annotation.Autowired;
import simsys.core.clock.Clock;
import simsys.core.environment.Environment;
import simsys.core.provider.EventProvider;

public class SimulationContextImpl implements SimulationContext {

  private double deltaTimeLastTwoEvents;

  @Autowired
  protected Environment environment;
  @Autowired
  protected Clock clock;
  @Autowired
  protected EventProvider eventProvider;

  public SimulationContextImpl() {
    this.deltaTimeLastTwoEvents = 0;
  }

  @Override
  public double getDeltaTimeLastTwoEvents() {
    return this.deltaTimeLastTwoEvents;
  }

  @Override
  public void setDeltaTimeLastTwoEvents(double delta) {
    this.deltaTimeLastTwoEvents = delta;
  }

  @Override
  public Environment getEnvironment() {
    return this.environment;
  }

  @Override
  public Clock getClock() {
    return this.clock;
  }

  @Override
  public EventProvider getEventProvider() {
    return this.eventProvider;
  }

}
