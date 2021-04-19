package simsys.core.agent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.springframework.util.ReflectionUtils;
import simsys.core.annotation.Action;
import simsys.core.annotation.State;
import simsys.core.annotation.Trigger;
import simsys.core.context.SimulationContext;
import simsys.core.event.HandledEvent;
import simsys.core.event.handler.StatisticStateHandler;

@Slf4j
public class AgentEventImpl implements AgentEvent {

  protected SimulationContext simulationContext;
  protected Agent agent;

  private Map<String, HandledEvent> eventResolver;

  public AgentEventImpl(SimulationContext simulationContext, Agent agent) {
    this.simulationContext = simulationContext;
    this.agent = agent;
    agentDefinition();
  }

  public void agentDefinition() {
    LOGGER.debug("Agent name: " + this.agent.getClass().getName());

    // create map State->Method
    this.eventResolver = new HashMap<>();
    Map<String, Method> methodResolver = new HashMap<>();

    defineAllAgentStates();
    String initialState = getInitialState();

    // create statistic handler for all states
    StatisticStateHandler statisticHandler =
        new StatisticStateHandler(this.agent, this.simulationContext);

    Method[] methods = this.agent.getClass().getDeclaredMethods();
    for (Method method : methods) {
      Annotation[] annotations = method.getDeclaredAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation.annotationType().equals(Action.class)) {
          String[] statesFromAnnotation = ((Action) annotation).states();
          for (String state : statesFromAnnotation) {
            if (!this.agent.getStates().contains(state)) {
              throw new IllegalStateException("There is no a state with name "
                  + state);
            }
            methodResolver.put(state, method);
            eventResolver.put(state, new HandledEvent());
          }
        }
      }
    }

    Field currentStateFiled = getField("currentState");
    Field nextStateField = getField("nextState");
    Field nextActivationTimeField = getField("nextActivationTime");

    // we create event per method
    for (Map.Entry<String, Method> response : methodResolver.entrySet()) {
      HandledEvent event = eventResolver.get(response.getKey());
      Method method = response.getValue();
      event
          .addHandler(statisticHandler)
          .addHandler(e -> {
        method.setAccessible(true);
        LOGGER.debug("Invoke method: " + method.getName());
        ReflectionUtils.invokeMethod(method, this.agent);

        if (method.isAnnotationPresent(Trigger.class)) {
//      TODO: необходимо достать SystemAgent из аннотации Trigger и вызвать у него метод
//       с именем methodName.
//       Вопрос: как получить объект SystemAgent, чтобы вызвать у него метод
         System.out.println("YEEES, TRIGGER!!! " + method.getName());
        }

        Object nextState = ReflectionUtils.getField(nextStateField, this.agent);
        ReflectionUtils.setField(currentStateFiled, this.agent, nextState);


        // it means the next state is defined = makes sense
        // we need create the next event
        if (nextState != null) {
          HandledEvent nextEvent = eventResolver.get(nextState);
          Object nextActivationTime = ReflectionUtils
              .getField(nextActivationTimeField, this.agent);
          nextEvent.setActivateTime((double) nextActivationTime);
          this.simulationContext.getEventProvider().add(nextEvent);
        }
      });
    }

    this.simulationContext.getEventProvider().add(eventResolver.get(initialState));
    ReflectionUtils.setField(currentStateFiled, this.agent, initialState);

//    printStateAndCorrespondingActions();
  }

  private void defineAllAgentStates() {
    this.agent.setStates(new HashSet<>());

    Field[] fields = this.agent.getClass().getDeclaredFields();
    for (Field field : fields) {
      Annotation[] annotations = field.getDeclaredAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation.annotationType().equals(State.class)) {
          field.setAccessible(true);
          String stateName = (String) ReflectionUtils.getField(field, this.agent);
          this.agent.getStates().add(stateName);
        }
      }
    }
  }

  private String getInitialState() {
    String initialState = null;
    Field[] fields = this.agent.getClass().getDeclaredFields();
    for (Field field : fields) {
      Annotation[] annotations = field.getDeclaredAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation.annotationType().equals(State.class)) {
          if (((State) annotation).initial()) {
            field.setAccessible(true);
            initialState = (String) ReflectionUtils.getField(field, this.agent);
          }
        }
      }
    }

    if (initialState == null) {
      throw new IllegalStateException("There is no initial state");
    }

    LOGGER.debug("Found the initial state of the agent: " + initialState);
    return initialState;
  }

  private Field getField(String nameField) {
    Field field = ReflectionUtils
        .findField(this.agent.getClass(),
            nameField);
    field.setAccessible(true);
    return field;
  }

  private void printStateAndCorrespondingActions() {
    LOGGER.debug("Print states and corresponding actions");
    for (Map.Entry<String, HandledEvent> pair : eventResolver.entrySet()) {
      LOGGER.debug(pair.getKey() + ": " + pair.getValue());
    }
    LOGGER.debug("*******************************************\n");
  }

  @Override
  public Agent getAgent() {
    return this.agent;
  }

  @Override
  public void setAgent(Agent agent) {
    this.agent = agent;
    agentDefinition();
  }

  @Override
  public double getActivateTime() {
    return 0;
  }

  @Override
  public void setActivateTime(double activateTime) {

  }

  @Override
  public void activate() {

  }
}
