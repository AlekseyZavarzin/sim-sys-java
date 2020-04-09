package simsys.core.model;

import lombok.extern.slf4j.Slf4j;
import simsys.core.event.Event;

import java.util.function.Predicate;

@Slf4j
public abstract class AbstractSimulationModel implements SimulationModel {

    protected SimulationContext simulationContext;
    protected Predicate<SimulationContext> stopCondition;

    @Override
    public void step() {
        Event nextEvent = simulationContext.getEventProvider().getNext();
        simulationContext.getClock().setCurrentTime(nextEvent.getActivateTime());
        LOGGER.debug("A new EVENT!  The current time: {}", simulationContext.getCurrentTime());
        nextEvent.activate();
    }

    @Override
    public void run() {
        while (!stopCondition.test(simulationContext)) {
            step();
        }
    }

    public Predicate<SimulationContext> getStopCondition() {
        return stopCondition;
    }

    public void setStopCondition(Predicate<SimulationContext> stopCondition) {
        this.stopCondition = stopCondition;
    }


}
